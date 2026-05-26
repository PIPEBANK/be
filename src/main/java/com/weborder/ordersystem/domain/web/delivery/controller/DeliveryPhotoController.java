package com.weborder.ordersystem.domain.web.delivery.controller;

import com.weborder.ordersystem.domain.web.delivery.entity.DeliveryPhoto;
import com.weborder.ordersystem.domain.web.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/delivery-photos")
@RequiredArgsConstructor
public class DeliveryPhotoController {

    private final DeliveryService deliveryService;

    @GetMapping("/{orderKey}")
    public ResponseEntity<List<Map<String, Object>>> getPhotos(@PathVariable String orderKey) {
        List<DeliveryPhoto> photos = deliveryService.getPhotos(orderKey);
        List<Map<String, Object>> response = photos.stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "orderKey", p.getOrderKey(),
                        "originalName", p.getOriginalName() != null ? p.getOriginalName() : "",
                        "createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : "",
                        "url", "/api/delivery-photos/files/" + p.getId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> servePhoto(@PathVariable Long id) {
        Path filePath = deliveryService.getPhotoFilePath(id);
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String fileName = filePath.getFileName().toString().toLowerCase();
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (fileName.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
        else if (fileName.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
        else if (fileName.endsWith(".webp")) mediaType = MediaType.parseMediaType("image/webp");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
