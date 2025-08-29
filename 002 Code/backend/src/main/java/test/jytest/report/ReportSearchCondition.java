package test.jytest.report;

import java.sql.Date;

public class ReportSearchCondition {

    private String reportId;       // 보고서 ID (VARCHAR PK)
    private String reportType;     // 보고서 유형
    private Date generatedDate;    // 생성일
    private String filePath;       // 보고서 파일 경로 (부분 검색 등)
    private String createdBy;      // 생성자

    public ReportSearchCondition(String reportId, String reportType, Date generatedDate,
                                 String filePath, String createdBy) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.filePath = filePath;
        this.createdBy = createdBy;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
