package test.jytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import test.jytest.asset.AssetService;
import test.jytest.assetMovement.AssetMovementService;
import test.jytest.member.MemberService;
import test.jytest.notification.NotificationService;
import test.jytest.report.ReportService;
import test.jytest.repository.*;
import test.jytest.repository.jdbctemplate.*;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    private final DataSource dataSource;

    public JdbcTemplateConfig(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AssetRepository assetRepository() {
        return new JdbcTemplateAssetRepository(dataSource);
    }

    @Bean
    public AssetService assetService() {
        return new AssetService(assetRepository());
    }

    @Bean
    public AssetMovementRepository assetMovementRepository() {
        return new JdbcTemplateAssetMovementRepository(dataSource);
    }

    @Bean
    public AssetMovementService assetMovementService() {
        return new AssetMovementService(assetMovementRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new JdbcTemplateMemberRepository(dataSource);
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public NotificationRepository notificationRepository() {
        return new JdbcTemplateNotificationRepository(dataSource);
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationService(notificationRepository());
    }

    @Bean
    public ReportRepository reportRepository() {
        return new JdbcTemplateReportRepository(dataSource);
    }

    @Bean
    public ReportService reportService() {
        return new ReportService(reportRepository());
    }


}
