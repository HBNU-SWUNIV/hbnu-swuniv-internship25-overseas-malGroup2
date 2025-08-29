package test.jytest.report;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 보고서 생성 - POST
    @PostMapping
    public ResponseEntity<Report> create(@RequestBody final ReportRequest request) {
        Report report = new Report(
                request.getReportId(),     // VARCHAR PK
                request.getReportType(),
                request.getGeneratedDate(),
                request.getFilePath(),
                request.getCreatedBy()
        );
        return ResponseEntity.ok(reportService.createReport(report));
    }

    // 전체 보고서 조회 - GET
    @GetMapping
    public ResponseEntity<List<Report>> getReports() {
        return ResponseEntity.ok(reportService.findReports());
    }

    // 보고서 단건 조회 - GET
    @GetMapping("/{report_id}")
    public ResponseEntity<Report> getReport(@PathVariable("report_id") final String reportId) {
        Optional<Report> opt = reportService.findReportById(reportId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opt.get());
    }

    // 검색 조건 조회 - GET
    @GetMapping("/search")
    public ResponseEntity<List<Report>> searchReports(final ReportSearchCondition cond) {
        return ResponseEntity.ok(reportService.search(cond));
    }

    // 보고서 수정 - PATCH
    @PatchMapping("/{report_id}")
    public ResponseEntity<String> updateReport(@PathVariable("report_id") final String reportId,
                                               @RequestBody final ReportRequest request) {
        Report update = new Report();
        update.setReportId(reportId);
        update.setReportType(request.getReportType());
        update.setGeneratedDate(request.getGeneratedDate());
        update.setFilePath(request.getFilePath());
        update.setCreatedBy(request.getCreatedBy());

        int updated = reportService.updateReport(update);
        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 보고서를 찾을 수 없거나 수정된 데이터가 없습니다.");
        }
        return ResponseEntity.ok("업데이트 성공");
    }

    // 보고서 삭제 - DELETE
    @DeleteMapping("/{report_id}")
    public ResponseEntity<String> removeReport(@PathVariable("report_id") final String reportId) {
        int deleted = reportService.deleteReport(reportId);
        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 보고서를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("보고서 삭제 성공");
    }
}
