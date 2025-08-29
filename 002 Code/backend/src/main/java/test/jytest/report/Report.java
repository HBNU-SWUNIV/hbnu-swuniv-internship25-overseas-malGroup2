package test.jytest.report;

import java.sql.Date;
import java.util.Objects;


public class Report {

    private String reportId;       // 보고서 ID (PK)
    private String reportType;     // 보고서 유형
    private Date generatedDate;    // 생성일
    private String filePath;       // 보고서 파일 경로
    private String createdBy;      // 생성자

    public Report() {}

    public Report(String reportId, String reportType, Date generatedDate,
                  String filePath, String createdBy) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.filePath = filePath;
        this.createdBy = createdBy;
    }

    /** PK 없이 생성(외부에서 ID 생성/세팅 예정인 경우) */
    public Report(String reportType, Date generatedDate,
                  String filePath, String createdBy) {
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

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reportType='" + reportType + '\'' +
                ", generatedDate=" + generatedDate +
                ", filePath='" + filePath + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(reportId, report.reportId) &&
                Objects.equals(reportType, report.reportType) &&
                Objects.equals(generatedDate, report.generatedDate) &&
                Objects.equals(filePath, report.filePath) &&
                Objects.equals(createdBy, report.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, reportType, generatedDate, filePath, createdBy);
    }
}
