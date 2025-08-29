package test.jytest.notification;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class NotificationRequest {

    private String notificationId;     // VARCHAR PK
    private String notificationType;   // 알림 유형
    private String assetId;            // 자산 ID (FK)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date triggerDate;          // 알림 발생일

    private String recipient;          // 수신자
    private String message;            // 알림 내용

    public NotificationRequest() {
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
