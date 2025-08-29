package test.jytest.notification;

import java.sql.Date;
import java.util.Objects;


public class Notification {

    private String notificationId;     // VARCHAR PK
    private String notificationType;   // VARCHAR(50)
    private String assetId;            // FK -> assets.asset_id
    private Date triggerDate;          // DATE
    private String recipient;          // VARCHAR(100)
    private String message;            // TEXT

    public Notification() {}

    public Notification(String notificationId, String notificationType, String assetId,
                        Date triggerDate, String recipient, String message) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.assetId = assetId;
        this.triggerDate = triggerDate;
        this.recipient = recipient;
        this.message = message;
    }

    /** PK 없는 생성자 (INSERT 시 notificationId를 자동 생성하거나 외부에서 세팅) */
    public Notification(String notificationType, String assetId,
                        Date triggerDate, String recipient, String message) {
        this.notificationType = notificationType;
        this.assetId = assetId;
        this.triggerDate = triggerDate;
        this.recipient = recipient;
        this.message = message;
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

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", assetId='" + assetId + '\'' +
                ", triggerDate=" + triggerDate +
                ", recipient='" + recipient + '\'' +
                ", message='" + (message != null ?
                (message.length() > 60 ? message.substring(0, 60) + "..." : message) : null) + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(notificationId, that.notificationId) &&
                Objects.equals(notificationType, that.notificationType) &&
                Objects.equals(assetId, that.assetId) &&
                Objects.equals(triggerDate, that.triggerDate) &&
                Objects.equals(recipient, that.recipient) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, notificationType, assetId,
                triggerDate, recipient, message);
    }
}
