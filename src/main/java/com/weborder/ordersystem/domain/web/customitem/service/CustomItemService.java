package com.weborder.ordersystem.domain.web.customitem.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemPriceRequest;
import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemRequest;
import com.weborder.ordersystem.domain.web.customitem.dto.CustomItemResponse;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItem;
import com.weborder.ordersystem.domain.web.customitem.entity.CustomItemImage;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemImageRepository;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemRepository;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "webTransactionManager")
public class CustomItemService {

    private final CustomItemRepository customItemRepository;
    private final CustomItemImageRepository customItemImageRepository;
    private final WebOrderTranRepository webOrderTranRepository;

    @Value("${app.upload.dir:uploads/items}")
    private String baseUploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int MAX_DIMENSION = 1200;
    private static final double JPEG_QUALITY = 0.82;
    private static final long MAX_FILE_BYTES = 500 * 1024;
    private static final int MAX_IMAGES = 3;

    public CustomItemResponse createCustomItem(Integer custCode, CustomItemRequest request,
                                                List<MultipartFile> images, String loginId) throws IOException {
        CustomItem item = CustomItem.builder()
                .customItemCust(custCode)
                .customItemHnam(request.getName())
                .customItemDesc(request.getDescription())
                .customItemFuser(loginId)
                .customItemLuser(loginId)
                .build();

        CustomItem saved = customItemRepository.save(item);
        log.info("커스텀 아이템 등록 - id: {}, cust: {}, name: {}", saved.getCustomItemCode(), custCode, request.getName());

        if (images != null && !images.isEmpty()) {
            int limit = Math.min(images.size(), MAX_IMAGES);
            for (int i = 0; i < limit; i++) {
                uploadImage(saved.getCustomItemCode(), custCode, images.get(i), i, i == 0);
            }
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<CustomItemResponse> getCustomItems(Integer custCode) {
        List<CustomItem> items = customItemRepository.findActiveByCust(custCode);
        return items.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public CustomItemResponse getCustomItem(Long customItemCode) {
        CustomItem item = customItemRepository.findById(customItemCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커스텀 아이템: " + customItemCode));
        return toResponse(item);
    }

    public void deactivateCustomItem(Long customItemCode, String loginId) {
        CustomItem item = customItemRepository.findById(customItemCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커스텀 아이템: " + customItemCode));
        item.deactivate(loginId);
        customItemRepository.save(item);
        log.info("커스텀 아이템 비활성화 - id: {}", customItemCode);
    }

    public CustomItemResponse updatePrice(Long customItemCode, CustomItemPriceRequest request, String loginId) {
        CustomItem item = customItemRepository.findById(customItemCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커스텀 아이템: " + customItemCode));
        item.updatePrice(request.getSrate(), request.getSpec(), request.getUnit(),
                request.getVatDiv(), request.getRemark(), loginId);
        CustomItem saved = customItemRepository.save(item);
        log.info("커스텀 아이템 단가 책정 - id: {}, rate: {}", customItemCode, request.getSrate());

        if (request.getOrderDate() != null && request.getOrderSeq() != null) {
            WebOrderTran.WebOrderTranId tranId = new WebOrderTran.WebOrderTranId(
                    request.getOrderDate(), request.getOrderSosok(),
                    request.getOrderUjcd(), request.getOrderAcno(), request.getOrderSeq());
            webOrderTranRepository.findById(tranId).ifPresent(tran -> {
                tran.updateCustomItemPrice(request.getSrate(), request.getSpec(), request.getUnit(), request.getVatDiv(), loginId);
                webOrderTranRepository.save(tran);
                log.info("WebOrderTran 가격 업데이트 - key: {}", tran.getOrderTranKey());
            });
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public Resource loadImageFile(Long imageId) throws IOException {
        CustomItemImage image = customItemImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지: " + imageId));
        Path filePath = Paths.get(image.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("파일을 읽을 수 없습니다: " + image.getFilePath());
        }
        return resource;
    }

    private void uploadImage(Long customItemId, Integer custCode, MultipartFile file,
                             int sortOrder, boolean isThumbnail) throws IOException {
        String uploadDir = baseUploadDir.replace("items", "custom-items");
        Path uploadPath = Paths.get(uploadDir, String.valueOf(custCode));
        Files.createDirectories(uploadPath);

        String savedName = UUID.randomUUID() + ".jpg";
        Path targetPath = uploadPath.resolve(savedName);

        InputStream input = optimizeImage(file);
        Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
        input.close();

        CustomItemImage image = CustomItemImage.builder()
                .customItemId(customItemId)
                .filePath(targetPath.toString())
                .originalName(file.getOriginalFilename())
                .sortOrder(sortOrder)
                .isThumbnail(isThumbnail)
                .build();

        customItemImageRepository.save(image);
    }

    private InputStream optimizeImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        double quality = JPEG_QUALITY;

        Thumbnails.of(file.getInputStream())
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .outputQuality(quality)
                .toOutputStream(out);

        while (out.size() > MAX_FILE_BYTES && quality > 0.4) {
            quality -= 0.1;
            out.reset();
            Thumbnails.of(file.getInputStream())
                    .size(MAX_DIMENSION, MAX_DIMENSION)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toOutputStream(out);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private CustomItemResponse toResponse(CustomItem item) {
        List<CustomItemImage> images = customItemImageRepository
                .findByCustomItemIdOrderBySortOrderAsc(item.getCustomItemCode());

        String thumbnailUrl = images.stream()
                .filter(CustomItemImage::getIsThumbnail)
                .findFirst()
                .map(img -> baseUrl + "/api/custom-items/images/files/" + img.getId())
                .orElse(images.isEmpty() ? null : baseUrl + "/api/custom-items/images/files/" + images.get(0).getId());

        List<String> imageUrls = images.stream()
                .map(img -> baseUrl + "/api/custom-items/images/files/" + img.getId())
                .collect(Collectors.toList());

        return CustomItemResponse.from(item, thumbnailUrl, imageUrls);
    }
}
