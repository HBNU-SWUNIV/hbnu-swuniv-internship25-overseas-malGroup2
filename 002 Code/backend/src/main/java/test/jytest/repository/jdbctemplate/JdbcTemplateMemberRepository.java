package test.jytest.repository.jdbctemplate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import test.jytest.member.Member;
import test.jytest.member.MemberSearchCondition;
import test.jytest.repository.MemberRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO customer (" +
                "customer_id, customer_type, customer_name" +
                ") VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                member.getCustomerId(),
                member.getCustomerType(),
                member.getCustomerName()
        );
        return member;
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM customer";
        return jdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public Optional<Member> findByCustomerId(String customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        try {
            Member member = jdbcTemplate.queryForObject(sql, rowMapper(), customerId);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Member> search(MemberSearchCondition cond) {
        StringBuilder sql = new StringBuilder("SELECT * FROM customer WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (cond.getCustomerId() != null && !cond.getCustomerId().isEmpty()) {
            sql.append(" AND customer_id LIKE ?");
            params.add("%" + cond.getCustomerId() + "%");
        }
        if (cond.getCustomerType() != null && !cond.getCustomerType().isEmpty()) {
            sql.append(" AND customer_type LIKE ?");
            params.add("%" + cond.getCustomerType() + "%");
        }
        if (cond.getCustomerName() != null && !cond.getCustomerName().isEmpty()) {
            sql.append(" AND customer_name LIKE ?");
            params.add("%" + cond.getCustomerName() + "%");
        }

        return jdbcTemplate.query(sql.toString(), rowMapper(), params.toArray());
    }

    @Override
    public int updateByCustomerId(String customerId, Member update) {
        String sql = "UPDATE customer SET " +
                "customer_type = ?, customer_name = ? " +
                "WHERE customer_id = ?";

        return jdbcTemplate.update(sql,
                update.getCustomerType(),
                update.getCustomerName(),
                customerId
        );
    }

    @Override
    public int deleteByCustomerId(String customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        return jdbcTemplate.update(sql, customerId);
    }

    private RowMapper<Member> rowMapper() {
        return (rs, rowNum) -> {
            Member m = new Member();
            m.setCustomerId(rs.getString("customer_id"));
            m.setCustomerType(rs.getString("customer_type"));
            m.setCustomerName(rs.getString("customer_name"));
            return m;
        };
    }
}
