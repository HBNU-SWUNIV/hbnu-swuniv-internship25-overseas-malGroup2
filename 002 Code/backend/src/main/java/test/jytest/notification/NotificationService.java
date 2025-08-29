package test.jytest.notification;

import test.jytest.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 알림 생성
    Notification createNotification(final Notification notification) {
        return notificationRepository.save(notification);
    }

    // 전체 알림 조회
    List<Notification> findNotifications() {
        return notificationRepository.findAll();
    }

    // 알림 ID로 조회 (VARCHAR PK)
    Optional<Notification> findNotificationById(final String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    // 알림 업데이트
    public int updateNotification(final Notification notification) {
        return notificationRepository.update(notification);
    }

    // 알림 삭제
    public int deleteNotification(final String notificationId) {
        return notificationRepository.delete(notificationId);
    }

    // 조건 검색
    public List<Notification> search(final NotificationSearchCondition cond) {
        return notificationRepository.search(cond);
    }
}
