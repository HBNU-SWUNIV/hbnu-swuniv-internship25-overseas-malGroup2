package test.jytest.repository.jdbctemplate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import test.jytest.assetMovement.AssetMovement;
import test.jytest.assetMovement.AssetMovementSearchCondition;
import test.jytest.repository.AssetMovementRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateAssetMovementRepository implements AssetMovementRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateAssetMovementRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public AssetMovement save(AssetMovement movement) {
        // movement_id (VARCHAR) 포함해서 저장
        String sql = "INSERT INTO asset_movements (" +
                "movement_id, asset_id, movement_type, date_taken_out, expected_return_date, date_returned, " +
                "person_taking_asset, department, purpose, remarks, condition_at_checkout, condition_at_checkin" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                movement.getMovementId(),
                movement.getAssetId(),
                movement.getMovementType(),
                movement.getDateTakenOut(),
                movement.getExpectedReturnDate(),
                movement.getDateReturned(),
                movement.getPersonTakingAsset(),
                movement.getDepartment(),
                movement.getPurpose(),
                movement.getRemarks(),
                movement.getConditionAtCheckout(),
                movement.getConditionAtCheckin()
        );
        return movement;
    }

    @Override
    public Optional<AssetMovement> findById(String movementId) {
        return Optional.empty();
    }

    @Override
    public Optional<AssetMovement> findByMovementId(String movementId) {
        String sql = "SELECT * FROM asset_movements WHERE movement_id = ?";
        try {
            AssetMovement movement = jdbcTemplate.queryForObject(sql, rowMapper(), movementId);
            return Optional.ofNullable(movement);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AssetMovement> findAll() {
        String sql = "SELECT * FROM asset_movements";
        return jdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public int update(AssetMovement assetMovement) {
        return 0;
    }

    @Override
    public int delete(String movementId) {
        return 0;
    }

    @Override
    public Optional<AssetMovement> findById(Long movementId) {
        return Optional.empty();
    }

    @Override
    public List<AssetMovement> search(AssetMovementSearchCondition cond) {
        StringBuilder sql = new StringBuilder("SELECT * FROM asset_movements WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (cond.getMovementId() != null) {
            sql.append(" AND movement_id LIKE ?");
            params.add("%" + cond.getMovementId() + "%");
        }
        if (cond.getAssetId() != null) {
            sql.append(" AND asset_id LIKE ?");
            params.add("%" + cond.getAssetId() + "%");
        }
        if (cond.getMovementType() != null) {
            sql.append(" AND movement_type LIKE ?");
            params.add("%" + cond.getMovementType() + "%");
        }
        if (cond.getDateTakenOut() != null) {
            sql.append(" AND date_taken_out = ?");
            params.add(cond.getDateTakenOut());
        }
        if (cond.getExpectedReturnDate() != null) {
            sql.append(" AND expected_return_date = ?");
            params.add(cond.getExpectedReturnDate());
        }
        if (cond.getDateReturned() != null) {
            sql.append(" AND date_returned = ?");
            params.add(cond.getDateReturned());
        }
        if (cond.getPersonTakingAsset() != null) {
            sql.append(" AND person_taking_asset LIKE ?");
            params.add("%" + cond.getPersonTakingAsset() + "%");
        }
        if (cond.getDepartment() != null) {
            sql.append(" AND department LIKE ?");
            params.add("%" + cond.getDepartment() + "%");
        }
        if (cond.getPurpose() != null) {
            sql.append(" AND purpose LIKE ?");
            params.add("%" + cond.getPurpose() + "%");
        }
        if (cond.getRemarks() != null) {
            sql.append(" AND remarks LIKE ?");
            params.add("%" + cond.getRemarks() + "%");
        }
        if (cond.getConditionAtCheckout() != null) {
            sql.append(" AND condition_at_checkout LIKE ?");
            params.add("%" + cond.getConditionAtCheckout() + "%");
        }
        if (cond.getConditionAtCheckin() != null) {
            sql.append(" AND condition_at_checkin LIKE ?");
            params.add("%" + cond.getConditionAtCheckin() + "%");
        }

        return jdbcTemplate.query(sql.toString(), rowMapper(), params.toArray());
    }

    @Override
    public int delete(Long movementId) {
        return 0;
    }

    @Override
    public int updateByMovementId(String movementId, AssetMovement movement) {
        String sql = "UPDATE asset_movements SET " +
                "asset_id = ?, movement_type = ?, date_taken_out = ?, expected_return_date = ?, date_returned = ?, " +
                "person_taking_asset = ?, department = ?, purpose = ?, remarks = ?, " +
                "condition_at_checkout = ?, condition_at_checkin = ? " +
                "WHERE movement_id = ?";

        return jdbcTemplate.update(sql,
                movement.getAssetId(),
                movement.getMovementType(),
                movement.getDateTakenOut(),
                movement.getExpectedReturnDate(),
                movement.getDateReturned(),
                movement.getPersonTakingAsset(),
                movement.getDepartment(),
                movement.getPurpose(),
                movement.getRemarks(),
                movement.getConditionAtCheckout(),
                movement.getConditionAtCheckin(),
                movementId
        );
    }

    @Override
    public int deleteByMovementId(String movementId) {
        String sql = "DELETE FROM asset_movements WHERE movement_id = ?";
        return jdbcTemplate.update(sql, movementId);
    }

    private RowMapper<AssetMovement> rowMapper() {
        return (rs, rowNum) -> {
            AssetMovement movement = new AssetMovement();
            movement.setMovementId(rs.getString("movement_id")); // 문자열
            movement.setAssetId(rs.getString("asset_id"));
            movement.setMovementType(rs.getString("movement_type"));
            movement.setDateTakenOut(rs.getDate("date_taken_out"));
            movement.setExpectedReturnDate(rs.getDate("expected_return_date"));
            movement.setDateReturned(rs.getDate("date_returned"));
            movement.setPersonTakingAsset(rs.getString("person_taking_asset"));
            movement.setDepartment(rs.getString("department"));
            movement.setPurpose(rs.getString("purpose"));
            movement.setRemarks(rs.getString("remarks"));
            movement.setConditionAtCheckout(rs.getString("condition_at_checkout"));
            movement.setConditionAtCheckin(rs.getString("condition_at_checkin"));
            return movement;
        };
    }
}
