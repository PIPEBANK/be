package com.weborder.ordersystem.domain.web.image.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.weborder.ordersystem.domain.web.image.dto.ItemImageResponse;
import com.weborder.ordersystem.domain.web.image.service.ItemImageService;

import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ItemImageController {

    private final ItemImageService itemImageService;

    /**
     * 이미지 업로드 (단건)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{itemCode}")
    public ResponseEntity<ItemImageResponse> uploadImage(
            @PathVariable Integer itemCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isThumbnail", required = false) Boolean isThumbnail,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        log.info("이미지 업로드 API - itemCode: {}, user: {}", itemCode, userDetails.getUsername());

        ItemImageResponse response = itemImageService.uploadImage(
                itemCode, file, isThumbnail, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 이미지 다건 업로드
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{itemCode}/batch")
    public ResponseEntity<List<ItemImageResponse>> uploadImages(
            @PathVariable Integer itemCode,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        log.info("이미지 다건 업로드 API - itemCode: {}, count: {}, user: {}",
                itemCode, files.size(), userDetails.getUsername());

        List<ItemImageResponse> responses = itemImageService.uploadImages(
                itemCode, files, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 특정 품목의 이미지 목록 조회
     */
    @GetMapping("/item/{itemCode}")
    public ResponseEntity<List<ItemImageResponse>> getImagesByItemCode(
            @PathVariable Integer itemCode) {
        List<ItemImageResponse> images = itemImageService.getImagesByItemCode(itemCode);
        return ResponseEntity.ok(images);
    }

    /**
     * 썸네일 이미지 조회
     */
    @GetMapping("/item/{itemCode}/thumbnail")
    public ResponseEntity<ItemImageResponse> getThumbnail(
            @PathVariable Integer itemCode) {
        ItemImageResponse thumbnail = itemImageService.getThumbnail(itemCode);
        if (thumbnail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(thumbnail);
    }

    /**
     * 이미지 파일 서빙 (브라우저에서 직접 접근)
     */
    @GetMapping("/files/{imageId}")
    public ResponseEntity<Resource> serveImage(@PathVariable Long imageId) throws IOException {
        Resource resource = itemImageService.loadImageFile(imageId);

        String contentType = determineContentType(resource.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }

    /**
     * 이미지 삭제 (단건)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        log.info("이미지 삭제 API - id: {}", imageId);
        itemImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 품목의 이미지 전체 삭제
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/item/{itemCode}")
    public ResponseEntity<Void> deleteAllByItemCode(@PathVariable Integer itemCode) {
        log.info("품목 이미지 전체 삭제 API - itemCode: {}", itemCode);
        itemImageService.deleteAllByItemCode(itemCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * 썸네일 지정
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{imageId}/thumbnail")
    public ResponseEntity<ItemImageResponse> setThumbnail(@PathVariable Long imageId) {
        log.info("썸네일 지정 API - id: {}", imageId);
        ItemImageResponse response = itemImageService.setThumbnail(imageId);
        return ResponseEntity.ok(response);
    }

    private String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}
