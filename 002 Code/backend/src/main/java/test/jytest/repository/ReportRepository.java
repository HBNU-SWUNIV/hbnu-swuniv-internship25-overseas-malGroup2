package test.jytest.repository;

import test.jytest.report.Report;
import test.jytest.report.ReportSearchCondition;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    /** 새로운 보고서 저장 */
    Report save(Report report);

    /** 전체 보고서 조회 */
    List<Report> findAll();

    /** reportId 기준 단건 조회 (VARCHAR PK) */
    Optional<Report> findById(String reportId);

    /** 검색 조건 기반 조회 */
    List<Report> search(ReportSearchCondition cond);

    /** 보고서 업데이트 */
    int update(Report update);

    /** reportId 기준 삭제 */
    int delete(String reportId);
}
