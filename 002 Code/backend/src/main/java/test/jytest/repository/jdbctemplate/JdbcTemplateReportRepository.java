package test.jytest.repository.jdbctemplate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import test.jytest.report.Report;
import test.jytest.report.ReportSearchCondition;
import test.jytest.repository.ReportRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateReportRepository implements ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateReportRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Report save(Report r) {
        String sql = "INSERT INTO report (" +
                "report_id, report_type, generated_date, file_path, created_by" +
                ") VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                r.getReportId(),
                r.getReportType(),
                r.getGeneratedDate(),
                r.getFilePath(),
                r.getCreatedBy()
        );
        return r;
    }

    @Override
    public List<Report> findAll() {
        String sql = "SELECT * FROM report";
        return jdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public Optional<Report> findById(String reportId) {
        String sql = "SELECT * FROM report WHERE report_id = ?";
        try {
            Report r = jdbcTemplate.queryForObject(sql, rowMapper(), reportId);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Report> search(ReportSearchCondition cond) {
        StringBuilder sql = new StringBuilder("SELECT * FROM report WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (cond.getReportId() != null && !cond.getReportId().isEmpty()) {
            sql.append(" AND report_id LIKE ?");
            params.add("%" + cond.getReportId() + "%");
        }
        if (cond.getReportType() != null && !cond.getReportType().isEmpty()) {
            sql.append(" AND report_type LIKE ?");
            params.add("%" + cond.getReportType() + "%");
        }
        if (cond.getGeneratedDate() != null) {
            sql.append(" AND generated_date = ?");
            params.add(cond.getGeneratedDate());
        }
        if (cond.getFilePath() != null && !cond.getFilePath().isEmpty()) {
            sql.append(" AND file_path LIKE ?");
            params.add("%" + cond.getFilePath() + "%");
        }
        if (cond.getCreatedBy() != null && !cond.getCreatedBy().isEmpty()) {
            sql.append(" AND created_by LIKE ?");
            params.add("%" + cond.getCreatedBy() + "%");
        }

        return jdbcTemplate.query(sql.toString(), rowMapper(), params.toArray());
    }

    @Override
    public int update(Report r) {
        String sql = "UPDATE report SET " +
                "report_type = ?, " +
                "generated_date = ?, " +
                "file_path = ?, " +
                "created_by = ? " +
                "WHERE report_id = ?";

        return jdbcTemplate.update(sql,
                r.getReportType(),
                r.getGeneratedDate(),
                r.getFilePath(),
                r.getCreatedBy(),
                r.getReportId()
        );
    }

    @Override
    public int delete(String reportId) {
        String sql = "DELETE FROM report WHERE report_id = ?";
        return jdbcTemplate.update(sql, reportId);
    }

    private RowMapper<Report> rowMapper() {
        return (rs, rowNum) -> {
            Report r = new Report();
            r.setReportId(rs.getString("report_id"));
            r.setReportType(rs.getString("report_type"));
            r.setGeneratedDate(rs.getDate("generated_date"));
            r.setFilePath(rs.getString("file_path"));
            r.setCreatedBy(rs.getString("created_by"));
            return r;
        };
    }
}
