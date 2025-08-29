package test.jytest.notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림 생성 - POST
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody final NotificationRequest request) {
        Notification notification = new Notification(
                request.getNotificationId(),   // VARCHAR PK
                request.getNotificationType(),
                request.getAssetId(),
                request.getTriggerDate(),
                request.getRecipient(),
                request.getMessage()
        );
        return ResponseEntity.ok(notificationService.createNotification(notification));
    }

    // 전체 알림 조회 - GET
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        return ResponseEntity.ok(notificationService.findNotifications());
    }

    // 알림 단건 조회 - GET
    @GetMapping("/{notification_id}")
    public ResponseEntity<Notification> getNotification(@PathVariable("notification_id") final String notificationId) {
        Optional<Notification> opt = notificationService.findNotificationById(notificationId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opt.get());
    }

    // 검색 조건으로 조회 - GET
    @GetMapping("/search")
    public ResponseEntity<List<Notification>> searchNotifications(NotificationSearchCondition cond) {
        return ResponseEntity.ok(notificationService.search(cond));
    }

    // 알림 수정 - PATCH
    @PatchMapping("/{notification_id}")
    public ResponseEntity<String> updateNotification(@PathVariable("notification_id") final String notificationId,
                                                     @RequestBody final NotificationRequest request) {
        Notification update = new Notification();
        update.setNotificationId(notificationId);
        update.setNotificationType(request.getNotificationType());
        update.setAssetId(request.getAssetId());
        update.setTriggerDate(request.getTriggerDate());
        update.setRecipient(request.getRecipient());
        update.setMessage(request.getMessage());

        int updated = notificationService.updateNotification(update);
        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 알림을 찾을 수 없거나 수정된 데이터가 없습니다.");
        }
        return ResponseEntity.ok("업데이트 성공");
    }

    // 알림 삭제 - DELETE
    @DeleteMapping("/{notification_id}")
    public ResponseEntity<String> removeNotification(@PathVariable("notification_id") final String notificationId) {
        int deleted = notificationService.deleteNotification(notificationId);
        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 알림을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("알림 삭제 성공");
    }
}
