package test.jytest.repository;

import test.jytest.notification.Notification;
import test.jytest.notification.NotificationSearchCondition;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    /** 새로운 알림 저장 */
    Notification save(Notification notification);

    /** 전체 알림 조회 */
    List<Notification> findAll();

    /** notificationId 기준 단건 조회 (VARCHAR PK) */
    Optional<Notification> findById(String notificationId);

    /** 검색 조건 기반 조회 */
    List<Notification> search(NotificationSearchCondition cond);

    /** 알림 업데이트 */
    int update(Notification update);

    /** notificationId 기준 삭제 */
    int delete(String notificationId);
}
