package com.weborder.ordersystem.domain.web.image.service;

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

import com.weborder.ordersystem.domain.web.customitem.entity.CustomItemImage;
import com.weborder.ordersystem.domain.web.customitem.repository.CustomItemImageRepository;
import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.entity.ItemImage;
import com.weborder.ordersystem.domain.web.image.repository.ItemImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "webTransactionManager")
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;
    private final CustomItemImageRepository customItemImageRepository;

    @Value("${app.upload.dir:uploads/items}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 이미지 업로드 (단건)
     */
    public ItemImageResponse uploadImage(Integer itemCode, MultipartFile file,
                                         Boolean isThumbnail, String createdBy) throws IOException {
        log.info("이미지 업로드 - itemCode: {}, fileName: {}, size: {}",
                itemCode, file.getOriginalFilename(), file.getSize());

        String savedPath = saveFile(itemCode, file);
        int nextOrder = (int) itemImageRepository.countByItemCode(itemCode);

        if (Boolean.TRUE.equals(isThumbnail)) {
            clearExistingThumbnail(itemCode);
        }

        ItemImage image = ItemImage.builder()
                .itemCode(itemCode)
                .filePath(savedPath)
                .originalName(file.getOriginalFilename())
                .sortOrder(nextOrder)
                .isThumbnail(isThumbnail != null ? isThumbnail : nextOrder == 0)
                .createdBy(createdBy)
                .build();

        ItemImage saved = itemImageRepository.save(image);
        log.info("이미지 저장 완료 - id: {}, path: {}", saved.getId(), savedPath);

        return ItemImageResponse.from(saved, baseUrl);
    }

    /**
     * 이미지 다건 업로드
     */
    public List<ItemImageResponse> uploadImages(Integer itemCode, List<MultipartFile> files,
                                                String createdBy) throws IOException {
        log.info("이미지 다건 업로드 - itemCode: {}, count: {}", itemCode, files.size());

        int currentCount = (int) itemImageRepository.countByItemCode(itemCode);

        return files.stream()
                .map(file -> {
                    try {
                        String savedPath = saveFile(itemCode, file);
                        int order = currentCount + files.indexOf(file);
                        boolean thumbnail = (currentCount == 0 && files.indexOf(file) == 0);

                        ItemImage image = ItemImage.builder()
                                .itemCode(itemCode)
                                .filePath(savedPath)
                                .originalName(file.getOriginalFilename())
                                .sortOrder(order)
                                .isThumbnail(thumbnail)
                                .createdBy(createdBy)
                                .build();

                        return ItemImageResponse.from(itemImageRepository.save(image), baseUrl);
                    } catch (IOException e) {
                        log.error("파일 저장 실패 - fileName: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 품목의 이미지 목록 조회
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<ItemImageResponse> getImagesByItemCode(Integer itemCode) {
        log.info("이미지 조회 - itemCode: {}", itemCode);

        return itemImageRepository.findByItemCodeOrderBySortOrderAsc(itemCode).stream()
                .map(image -> ItemImageResponse.from(image, baseUrl))
                .collect(Collectors.toList());
    }

    /**
     * 썸네일 이미지 조회
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public ItemImageResponse getThumbnail(Integer itemCode) {
        return itemImageRepository.findByItemCodeAndIsThumbnailTrue(itemCode)
                .map(image -> ItemImageResponse.from(image, baseUrl))
                .orElse(null);
    }

    /**
     * 커스텀 품목 썸네일 URL 조회
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public String getCustomItemThumbnailUrl(Long customItemId) {
        List<CustomItemImage> images = customItemImageRepository
                .findByCustomItemIdOrderBySortOrderAsc(customItemId);
        return images.stream()
                .filter(CustomItemImage::getIsThumbnail)
                .findFirst()
                .map(img -> baseUrl + "/api/custom-items/images/files/" + img.getId())
                .orElse(images.isEmpty() ? null : baseUrl + "/api/custom-items/images/files/" + images.get(0).getId());
    }

    /**
     * 이미지 파일 로드 (다운로드/서빙용)
     */
    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public Resource loadImageFile(Long imageId) throws IOException {
        ItemImage image = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다: " + imageId));

        Path filePath = Paths.get(image.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("파일을 읽을 수 없습니다: " + image.getFilePath());
        }

        return resource;
    }

    /**
     * 이미지 삭제 (단건)
     */
    public void deleteImage(Long imageId) {
        log.info("이미지 삭제 - id: {}", imageId);

        ItemImage image = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다: " + imageId));

        deleteFileQuietly(image.getFilePath());
        itemImageRepository.delete(image);
    }

    /**
     * 특정 품목의 이미지 전체 삭제
     */
    public void deleteAllByItemCode(Integer itemCode) {
        log.info("품목 이미지 전체 삭제 - itemCode: {}", itemCode);

        List<ItemImage> images = itemImageRepository.findByItemCodeOrderBySortOrderAsc(itemCode);
        images.forEach(image -> deleteFileQuietly(image.getFilePath()));
        itemImageRepository.deleteByItemCode(itemCode);
    }

    /**
     * 썸네일 지정
     */
    public ItemImageResponse setThumbnail(Long imageId) {
        ItemImage image = itemImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다: " + imageId));

        clearExistingThumbnail(image.getItemCode());
        image.setAsThumbnail();
        ItemImage saved = itemImageRepository.save(image);

        return ItemImageResponse.from(saved, baseUrl);
    }

    // ========== Private Methods ==========

    private static final int MAX_DIMENSION = 1200;
    private static final double JPEG_QUALITY = 0.82;
    private static final long MAX_FILE_BYTES = 500 * 1024; // 500KB

    private String saveFile(Integer itemCode, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir, String.valueOf(itemCode));
        Files.createDirectories(uploadPath);

        String savedName = UUID.randomUUID() + ".jpg";
        Path targetPath = uploadPath.resolve(savedName);

        InputStream input = optimizeImage(file);
        Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
        input.close();

        return targetPath.toString();
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

        log.info("이미지 최적화 - 원본: {}KB → 압축: {}KB (quality: {})",
                file.getSize() / 1024, out.size() / 1024, String.format("%.1f", quality));

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private void clearExistingThumbnail(Integer itemCode) {
        itemImageRepository.findByItemCodeAndIsThumbnailTrue(itemCode)
                .ifPresent(existing -> {
                    existing.unsetThumbnail();
                    itemImageRepository.save(existing);
                });
    }

    private void deleteFileQuietly(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.warn("파일 삭제 실패 - path: {}", filePath, e);
        }
    }
}
