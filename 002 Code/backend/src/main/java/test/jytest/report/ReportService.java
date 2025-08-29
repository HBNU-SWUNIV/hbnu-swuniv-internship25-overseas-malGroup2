package test.jytest.report;

import test.jytest.repository.ReportRepository;

import java.util.List;
import java.util.Optional;

public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 보고서 생성
    Report createReport(final Report report) {
        return reportRepository.save(report);
    }

    // 전체 보고서 조회
    List<Report> findReports() {
        return reportRepository.findAll();
    }

    // 보고서 ID로 조회 (VARCHAR PK)
    Optional<Report> findReportById(final String reportId) {
        return reportRepository.findById(reportId);
    }

    // 보고서 업데이트
    public int updateReport(final Report report) {
        return reportRepository.update(report);
    }

    // 보고서 삭제
    public int deleteReport(final String reportId) {
        return reportRepository.delete(reportId);
    }

    // 조건 검색
    public List<Report> search(final ReportSearchCondition cond) {
        return reportRepository.search(cond);
    }
}
