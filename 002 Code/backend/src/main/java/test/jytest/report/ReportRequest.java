package test.jytest.report;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class ReportRequest {

    private String reportId;        // VARCHAR(50) PK
    private String reportType;      // 보고서 유형

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date generatedDate;     // 생성일

    private String filePath;        // 보고서 파일 경로
    private String createdBy;       // 생성자

    public ReportRequest() {}

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
