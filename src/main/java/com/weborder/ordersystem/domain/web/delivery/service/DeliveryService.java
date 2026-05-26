package com.weborder.ordersystem.domain.web.delivery.service;

import com.weborder.ordersystem.domain.erp.repository.CustomerRepository;
import com.weborder.ordersystem.domain.web.delivery.entity.DeliveryPhoto;
import com.weborder.ordersystem.domain.web.delivery.repository.DeliveryPhotoRepository;
import com.weborder.ordersystem.domain.web.member.entity.Member;
import com.weborder.ordersystem.domain.web.member.entity.MemberRole;
import com.weborder.ordersystem.domain.web.member.repository.MemberRepository;
import com.weborder.ordersystem.domain.web.notification.service.NotificationService;
import com.weborder.ordersystem.domain.web.order.dto.OrderMastResponse;
import com.weborder.ordersystem.domain.web.order.dto.OrderTranResponse;
import com.weborder.ordersystem.domain.web.order.entity.GuestOrder;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderMast;
import com.weborder.ordersystem.domain.web.order.entity.WebOrderTran;
import com.weborder.ordersystem.domain.web.order.repository.GuestOrderRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderMastRepository;
import com.weborder.ordersystem.domain.web.order.repository.WebOrderTranRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WebOrderMastRepository webOrderMastRepository;
    private final WebOrderTranRepository webOrderTranRepository;
    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryPhotoRepository deliveryPhotoRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final NotificationService notificationService;

    @Value("${app.upload.dir:uploads/items}")
    private String uploadDir;

    // ===================== 관리자 API =====================

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse confirmOrder(String orderKey, Long driverId, String adminUser) {
        WebOrderMast mast = findMastByKey(orderKey);

        if (!"ORDERED".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("주문접수 상태에서만 접수 처리 가능합니다. 현재: " + mast.getWebOrderStatus());
        }

        Member driver = memberRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배송기사입니다"));
        if (driver.getRole() != MemberRole.DRIVER && driver.getRole() != MemberRole.ADMIN) {
            throw new IllegalArgumentException("배송기사 또는 관리자 권한의 회원만 지정 가능합니다");
        }

        mast.assignDriver(driverId, adminUser);
        webOrderMastRepository.save(mast);
        log.info("주문 접수완료 - key: {}, driver: {}", orderKey, driverId);

        // 알림: 배송기사에게 (본인이 본인을 배정한 경우 제외)
        Member adminMember = memberRepository.findByMemberId(adminUser).orElse(null);
        if (adminMember == null || !adminMember.getId().equals(driverId)) {
            notificationService.send(driverId, "DELIVERY_ASSIGNED",
                    "배송 배정", "새로운 배송이 배정되었습니다.", orderKey);
        }
        // 알림: 거래처 소속 회원에게
        sendNotificationToCustMembers(mast, "ORDER_CONFIRMED",
                "접수완료", "주문이 접수되었습니다. 배송 준비중입니다.", orderKey);

        return buildResponse(mast);
    }

    public List<Member> getDriverList() {
        return memberRepository.findByRoleInAndUseYnTrue(List.of(MemberRole.DRIVER, MemberRole.ADMIN));
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public Map<String, Object> getAssignedOrdersPaged(String startDate, String endDate, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();

        String sd = hasStart ? startDate : "00000000";
        String ed = hasEnd ? endDate : "99999999";
        boolean hasDate = hasStart || hasEnd;

        Page<WebOrderMast> result;
        if (hasDate && hasStatus) {
            result = webOrderMastRepository.findAssignedOrdersByDateRangeAndStatusPaged(sd, ed, status, pageable);
        } else if (hasDate) {
            result = webOrderMastRepository.findAssignedOrdersByDateRangePaged(sd, ed, pageable);
        } else if (hasStatus) {
            result = webOrderMastRepository.findAssignedOrdersByStatusPaged(status, pageable);
        } else {
            result = webOrderMastRepository.findAssignedOrdersPaged(pageable);
        }

        List<OrderMastResponse> content = result.getContent().stream()
                .map(m -> {
                    try { return buildResponse(m); }
                    catch (Exception e) { log.error("배송 응답 생성 실패 - key: {}", m.getOrderKey(), e); return null; }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("page", result.getNumber());
        response.put("size", result.getSize());
        return response;
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public OrderMastResponse getAdminDeliveryDetail(String orderKey) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (mast.getWebDriverId() == null) {
            throw new IllegalArgumentException("배정되지 않은 주문입니다");
        }
        return buildResponse(mast);
    }

    // ===================== 배송기사 API =====================

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public Map<String, Object> getMyDeliveriesPaged(Long driverId, String startDate, String endDate, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();

        String sd = hasStart ? startDate : "00000000";
        String ed = hasEnd ? endDate : "99999999";
        boolean hasDate = hasStart || hasEnd;

        Page<WebOrderMast> result;
        if (hasDate && hasStatus) {
            result = webOrderMastRepository.findByDriverAndDateRangeAndStatusPaged(driverId, sd, ed, status, pageable);
        } else if (hasDate) {
            result = webOrderMastRepository.findByDriverAndDateRangePaged(driverId, sd, ed, pageable);
        } else if (hasStatus) {
            result = webOrderMastRepository.findByDriverIdAndStatusPaged(driverId, status, pageable);
        } else {
            result = webOrderMastRepository.findByDriverIdPaged(driverId, pageable);
        }

        List<OrderMastResponse> content = result.getContent().stream()
                .map(m -> {
                    try { return buildResponse(m); }
                    catch (Exception e) { log.error("배송기사 응답 생성 실패 - key: {}", m.getOrderKey(), e); return null; }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("page", result.getNumber());
        response.put("size", result.getSize());
        return response;
    }

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public OrderMastResponse getDeliveryDetail(String orderKey, Long driverId) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!driverId.equals(mast.getWebDriverId())) {
            throw new IllegalArgumentException("본인에게 배정된 배송만 조회 가능합니다");
        }
        return buildResponse(mast);
    }

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse startDelivery(String orderKey, Long driverId) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!driverId.equals(mast.getWebDriverId())) {
            throw new IllegalArgumentException("본인에게 배정된 배송만 처리 가능합니다");
        }
        if (!"CONFIRMED".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("접수완료 상태에서만 배송 출발이 가능합니다");
        }

        mast.updateStatus("SHIPPING", String.valueOf(driverId));
        webOrderMastRepository.save(mast);
        log.info("배송 출발 - key: {}", orderKey);

        sendNotificationToCustMembers(mast, "ORDER_SHIPPING",
                "배송중", "주문하신 상품이 배송 출발했습니다.", orderKey);

        return buildResponse(mast);
    }

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse completeDelivery(String orderKey, Long driverId, List<MultipartFile> photos, String driverSign) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!driverId.equals(mast.getWebDriverId())) {
            throw new IllegalArgumentException("본인에게 배정된 배송만 처리 가능합니다");
        }
        if (!"SHIPPING".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("배송중 상태에서만 배송 완료가 가능합니다");
        }
        if (photos == null || photos.isEmpty()) {
            throw new IllegalArgumentException("배송완료 시 사진을 최소 1장 첨부해주세요");
        }

        if (driverSign != null && !driverSign.isBlank()) {
            mast.completeWithSign(driverSign, String.valueOf(driverId), driverId);
        } else {
            mast.updateStatus("DELIVERED", String.valueOf(driverId));
        }
        webOrderMastRepository.save(mast);

        String driverLoginId = memberRepository.findById(driverId)
                .map(Member::getMemberId).orElse(String.valueOf(driverId));

        for (MultipartFile photo : photos) {
            saveDeliveryPhoto(orderKey, photo, driverLoginId);
        }

        log.info("배송 완료 - key: {}, 사진 {}장", orderKey, photos.size());

        // 알림: 거래처 소속 회원 + 관리자들
        sendNotificationToCustMembers(mast, "ORDER_DELIVERED",
                "배송완료", "주문하신 상품이 배송 완료되었습니다.", orderKey);
        List<Member> admins = memberRepository.findByRoleAndUseYnTrue(MemberRole.ADMIN);
        for (Member admin : admins) {
            notificationService.send(admin.getId(), "ORDER_DELIVERED",
                    "배송완료", "배송이 완료되었습니다. (" + orderKey + ")", orderKey);
        }

        return buildResponse(mast);
    }

    // ===================== 관리자 배송출발/완료 =====================

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse adminStartDelivery(String orderKey, Long adminMemberId) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!adminMemberId.equals(mast.getWebDriverId())) {
            throw new IllegalArgumentException("본인에게 배정된 배송만 처리 가능합니다");
        }
        if (!"CONFIRMED".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("접수완료 상태에서만 배송 출발이 가능합니다");
        }

        mast.updateStatus("SHIPPING", String.valueOf(adminMemberId));
        webOrderMastRepository.save(mast);
        log.info("관리자 배송 출발 - key: {}", orderKey);

        sendNotificationToCustMembers(mast, "ORDER_SHIPPING",
                "배송중", "주문하신 상품이 배송 출발했습니다.", orderKey);

        return buildResponse(mast);
    }

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse adminCompleteDelivery(String orderKey, Long adminMemberId,
                                                    List<MultipartFile> photos, String driverSign) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!adminMemberId.equals(mast.getWebDriverId())) {
            throw new IllegalArgumentException("본인에게 배정된 배송만 처리 가능합니다");
        }
        if (!"SHIPPING".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("배송중 상태에서만 배송 완료가 가능합니다");
        }
        if (photos == null || photos.isEmpty()) {
            throw new IllegalArgumentException("배송완료 시 사진을 최소 1장 첨부해주세요");
        }

        if (driverSign != null && !driverSign.isBlank()) {
            mast.completeWithSign(driverSign, String.valueOf(adminMemberId), adminMemberId);
        } else {
            mast.updateStatus("DELIVERED", String.valueOf(adminMemberId));
        }
        webOrderMastRepository.save(mast);

        String adminLoginId = memberRepository.findById(adminMemberId)
                .map(Member::getMemberId).orElse(String.valueOf(adminMemberId));

        for (MultipartFile photo : photos) {
            saveDeliveryPhoto(orderKey, photo, adminLoginId);
        }

        log.info("관리자 배송 완료 - key: {}, 사진 {}장", orderKey, photos.size());

        sendNotificationToCustMembers(mast, "ORDER_DELIVERED",
                "배송완료", "주문하신 상품이 배송 완료되었습니다.", orderKey);
        List<Member> admins = memberRepository.findByRoleAndUseYnTrue(MemberRole.ADMIN);
        for (Member admin : admins) {
            if (!admin.getId().equals(adminMemberId)) {
                notificationService.send(admin.getId(), "ORDER_DELIVERED",
                        "배송완료", "배송이 완료되었습니다. (" + orderKey + ")", orderKey);
            }
        }

        return buildResponse(mast);
    }

    // ===================== 거래처 확인서명 =====================

    @Transactional(transactionManager = "webTransactionManager")
    public OrderMastResponse submitCustSign(String orderKey, Long memberId, String signData) {
        WebOrderMast mast = findMastByKey(orderKey);
        if (!"DELIVERED".equals(mast.getWebOrderStatus())) {
            throw new IllegalStateException("배송완료 상태에서만 확인서명이 가능합니다");
        }

        // ORDER_MAST_CUST 기준으로 권한 검증 (같은 거래처 소속 회원이면 서명 가능)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Integer memberCustCode = null;
        if (member.getCustCode() != null && !member.getCustCode().isBlank()) {
            try { memberCustCode = Integer.parseInt(member.getCustCode().trim()); }
            catch (NumberFormatException ignored) {}
        }
        if (memberCustCode == null || !memberCustCode.equals(mast.getOrderMastCust())) {
            throw new IllegalArgumentException("주문한 거래처만 확인서명할 수 있습니다");
        }

        if (mast.getWebCustSign() != null && !mast.getWebCustSign().isBlank()) {
            throw new IllegalStateException("이미 확인서명이 완료되었습니다");
        }
        if (signData == null || signData.isBlank()) {
            throw new IllegalArgumentException("서명 데이터가 필요합니다");
        }

        mast.submitCustSign(signData, memberId);
        webOrderMastRepository.save(mast);
        log.info("거래처 확인서명 완료 - key: {}, memberId: {}", orderKey, memberId);

        return buildResponse(mast);
    }

    // ===================== 사진 API =====================

    @Transactional(readOnly = true, transactionManager = "webTransactionManager")
    public List<DeliveryPhoto> getPhotos(String orderKey) {
        return deliveryPhotoRepository.findByOrderKeyOrderByCreatedAtAsc(orderKey);
    }

    public Path getPhotoFilePath(Long photoId) {
        DeliveryPhoto photo = deliveryPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사진입니다"));
        return Paths.get(photo.getFilePath());
    }

    // ===================== 내부 메서드 =====================

    /**
     * ORDER_MAST_CUST 기준으로 해당 거래처 소속 회원에게 알림 발송
     */
    private void sendNotificationToCustMembers(WebOrderMast mast, String type, String title, String message, String orderKey) {
        if (mast.getOrderMastCust() != null && mast.getOrderMastCust() > 0) {
            List<Member> custMembers = memberRepository.findByCustCodeAndUseYn(String.valueOf(mast.getOrderMastCust()), true);
            for (Member m : custMembers) {
                notificationService.send(m.getId(), type, title, message, orderKey);
            }
        }
    }

    private WebOrderMast findMastByKey(String orderKey) {
        String[] parts = orderKey.split("-");
        if (parts.length != 4) {
            throw new IllegalArgumentException("잘못된 주문키 형식입니다: " + orderKey);
        }
        WebOrderMast.WebOrderMastId id = new WebOrderMast.WebOrderMastId(
                parts[0], Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3]));
        return webOrderMastRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다: " + orderKey));
    }

    private OrderMastResponse buildResponse(WebOrderMast mast) {
        List<WebOrderTran> trans = webOrderTranRepository
                .findByOrderTranDateAndOrderTranSosokAndOrderTranUjcdAndOrderTranAcnoOrderByOrderTranSeqAsc(
                        mast.getOrderMastDate(), mast.getOrderMastSosok(),
                        mast.getOrderMastUjcd(), mast.getOrderMastAcno());

        List<OrderTranResponse> tranResponses = trans.stream()
                .map(OrderTranResponse::from)
                .collect(Collectors.toList());

        BigDecimal total = tranResponses.stream()
                .map(OrderTranResponse::getTot)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ORDER_MAST_CUST 기준 거래처 정보 조회 (통일)
        String custName = null;
        String custAddr = null;
        if (mast.getOrderMastCust() != null && mast.getOrderMastCust() > 0) {
            try {
                var custOpt = customerRepository.findById(mast.getOrderMastCust());
                if (custOpt.isPresent()) {
                    custName = custOpt.get().getDisplayName();
                    custAddr = custOpt.get().getCustCodeAddr();
                }
            } catch (Exception e) {
                log.warn("거래처 정보 조회 실패 - custCode: {}", mast.getOrderMastCust());
            }
        }

        // 비회원(custCode==0)인 경우 GuestOrder에서 조회
        GuestOrder guest = null;
        if (mast.getOrderMastCust() == null || mast.getOrderMastCust() == 0) {
            guest = guestOrderRepository.findByOrderKey(mast.getOrderKey()).orElse(null);
            if (guest != null) {
                custName = guest.getCompanyName();
                custAddr = guest.getAddress();
            }
        }

        String driverName = null;
        String driverMemberId = null;
        if (mast.getWebDriverId() != null) {
            Member driver = memberRepository.findById(mast.getWebDriverId()).orElse(null);
            if (driver != null) {
                driverName = driver.getMemberName();
                driverMemberId = driver.getMemberId();
            }
        }

        OrderMastResponse.OrderMastResponseBuilder builder = OrderMastResponse.builder()
                .orderDate(mast.getOrderMastDate())
                .sosok(mast.getOrderMastSosok())
                .ujcd(mast.getOrderMastUjcd())
                .acno(mast.getOrderMastAcno())
                .cust(mast.getOrderMastCust())
                .sawon(mast.getOrderMastSawon())
                .odate(mast.getOrderMastOdate())
                .project(mast.getOrderMastProject())
                .remark(mast.getOrderMastRemark())
                .fdate(mast.getOrderMastFdate() != null
                        ? mast.getOrderMastFdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                .fuser(mast.getOrderMastFuser())
                .ldate(mast.getOrderMastLdate() != null
                        ? mast.getOrderMastLdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
                .luser(mast.getOrderMastLuser())
                .webMemberId(mast.getWebMemberId())
                .webOrderStatus(mast.getWebOrderStatus())
                .orderKey(mast.getOrderKey())
                .items(tranResponses)
                .itemCount(tranResponses.size())
                .totalAmount(total)
                .memberName(custName)
                .custCodeName(custName)
                .custCodeAddr(custAddr)
                .webDriverId(mast.getWebDriverId())
                .driverName(driverName)
                .driverMemberId(driverMemberId)
                .confirmedAt(mast.getWebConfirmedAt() != null
                        ? mast.getWebConfirmedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .driverSign(mast.getWebDriverSign())
                .driverSignAt(mast.getWebDriverSignAt() != null
                        ? mast.getWebDriverSignAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .driverSignName(mast.getWebDriverSignMemberId() != null
                        ? memberRepository.findById(mast.getWebDriverSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null)
                .custSign(mast.getWebCustSign())
                .custSignAt(mast.getWebCustSignAt() != null
                        ? mast.getWebCustSignAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .custSignName(mast.getWebCustSignMemberId() != null
                        ? memberRepository.findById(mast.getWebCustSignMemberId())
                                .map(Member::getMemberName).orElse(null) : null);

        if (guest != null) {
            builder.guestCompanyName(guest.getCompanyName())
                   .guestManagerName(guest.getManagerName())
                   .guestContact(guest.getContact())
                   .guestAddress(guest.getAddress());
        }

        return builder.build();
    }


    private static final int MAX_DIMENSION = 1200;
    private static final double JPEG_QUALITY = 0.82;
    private static final long MAX_FILE_BYTES = 500 * 1024;

    private void saveDeliveryPhoto(String orderKey, MultipartFile file, String createdBy) {
        try {
            Path basePath = Paths.get(uploadDir).toAbsolutePath().getParent();
            Path dir = basePath.resolve("delivery").resolve(orderKey);
            Files.createDirectories(dir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);

            InputStream optimized = optimizeImage(file);
            Files.copy(optimized, filePath, StandardCopyOption.REPLACE_EXISTING);
            optimized.close();

            DeliveryPhoto photo = DeliveryPhoto.builder()
                    .orderKey(orderKey)
                    .filePath(filePath.toString())
                    .originalName(file.getOriginalFilename())
                    .createdBy(createdBy)
                    .build();
            deliveryPhotoRepository.save(photo);
        } catch (IOException e) {
            log.error("배송 사진 저장 실패 - orderKey: {}", orderKey, e);
            throw new RuntimeException("사진 저장에 실패했습니다", e);
        }
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

        log.info("배송 사진 최적화 - 원본: {}KB → 압축: {}KB (quality: {})",
                file.getSize() / 1024, out.size() / 1024, String.format("%.1f", quality));

        return new ByteArrayInputStream(out.toByteArray());
    }
}
