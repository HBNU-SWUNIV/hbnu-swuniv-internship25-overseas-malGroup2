package test.jytest.repository.jdbctemplate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import test.jytest.notification.Notification;
import test.jytest.notification.NotificationSearchCondition;
import test.jytest.repository.NotificationRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateNotificationRepository implements NotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateNotificationRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Notification save(Notification n) {
        String sql = "INSERT INTO notifications (" +
                "notification_id, notification_type, asset_id, trigger_date, recipient, message" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                n.getNotificationId(),
                n.getNotificationType(),
                n.getAssetId(),
                n.getTriggerDate(),
                n.getRecipient(),
                n.getMessage()
        );
        return n;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notifications";
        return jdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public Optional<Notification> findById(String notificationId) {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        try {
            Notification n = jdbcTemplate.queryForObject(sql, rowMapper(), notificationId);
            return Optional.ofNullable(n);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Notification> search(NotificationSearchCondition cond) {
        StringBuilder sql = new StringBuilder("SELECT * FROM notifications WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (cond.getNotificationId() != null && !cond.getNotificationId().isEmpty()) {
            sql.append(" AND notification_id LIKE ?");
            params.add("%" + cond.getNotificationId() + "%");
        }
        if (cond.getNotificationType() != null && !cond.getNotificationType().isEmpty()) {
            sql.append(" AND notification_type LIKE ?");
            params.add("%" + cond.getNotificationType() + "%");
        }
        if (cond.getAssetId() != null && !cond.getAssetId().isEmpty()) {
            sql.append(" AND asset_id LIKE ?");
            params.add("%" + cond.getAssetId() + "%");
        }
        if (cond.getTriggerDate() != null) {
            sql.append(" AND trigger_date = ?");
            params.add(cond.getTriggerDate());
        }
        if (cond.getRecipient() != null && !cond.getRecipient().isEmpty()) {
            sql.append(" AND recipient LIKE ?");
            params.add("%" + cond.getRecipient() + "%");
        }
        if (cond.getMessage() != null && !cond.getMessage().isEmpty()) {
            sql.append(" AND message LIKE ?");
            params.add("%" + cond.getMessage() + "%");
        }

        return jdbcTemplate.query(sql.toString(), rowMapper(), params.toArray());
    }

    @Override
    public int update(Notification n) {
        String sql = "UPDATE notifications SET " +
                "notification_type = ?, " +
                "asset_id = ?, " +
                "trigger_date = ?, " +
                "recipient = ?, " +
                "message = ? " +
                "WHERE notification_id = ?";

        return jdbcTemplate.update(sql,
                n.getNotificationType(),
                n.getAssetId(),
                n.getTriggerDate(),
                n.getRecipient(),
                n.getMessage(),
                n.getNotificationId()
        );
    }

    @Override
    public int delete(String notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        return jdbcTemplate.update(sql, notificationId);
    }

    private RowMapper<Notification> rowMapper() {
        return (rs, rowNum) -> {
            Notification n = new Notification();
            n.setNotificationId(rs.getString("notification_id"));
            n.setNotificationType(rs.getString("notification_type"));
            n.setAssetId(rs.getString("asset_id"));
            n.setTriggerDate(rs.getDate("trigger_date"));
            n.setRecipient(rs.getString("recipient"));
            n.setMessage(rs.getString("message"));
            return n;
        };
    }
}
