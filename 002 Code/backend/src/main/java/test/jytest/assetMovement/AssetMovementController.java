package test.jytest.assetMovement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asset-movements")
public class AssetMovementController {

    private final AssetMovementService assetMovementService;

    public AssetMovementController(AssetMovementService assetMovementService) {
        this.assetMovementService = assetMovementService;
    }

    // 1) 자산 이동 추가 - POST
    @PostMapping
    public ResponseEntity<AssetMovement> createMovement(@RequestBody AssetMovementRequest request) {
        // movementId = 시리얼 넘버(문자열)
        AssetMovement movement = new AssetMovement(
                request.getMovementId(),        // ★ 식별자 = movementId (serial number)
                request.getAssetId(),
                request.getMovementType(),
                request.getDateTakenOut(),
                request.getExpectedReturnDate(),
                request.getDateReturned(),
                request.getPersonTakingAsset(),
                request.getDepartment(),
                request.getPurpose(),
                request.getRemarks(),
                request.getConditionAtCheckout(),
                request.getConditionAtCheckin()
        );

        AssetMovement saved = assetMovementService.createAssetMovement(movement);
        return ResponseEntity
                .created(URI.create("/api/asset-movements/" + saved.getMovementId()))
                .body(saved);
    }

    // 2) 전체 이동 내역 조회 - GET
    @GetMapping
    public ResponseEntity<List<AssetMovement>> getAllMovements() {
        return ResponseEntity.ok(assetMovementService.findAllAssetMovement());
    }

    // 3) 특정 이동(movementId) 조회 - GET
    @GetMapping("/{movementId}")
    public ResponseEntity<AssetMovement> getMovement(@PathVariable String movementId) {
        Optional<AssetMovement> movementOpt = assetMovementService.findAssetMovementByMovementId(movementId);
        return movementOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4) 이동 내역 검색 - GET (/search?assetId=...&movementType=... 등)
    @GetMapping("/search")
    public ResponseEntity<List<AssetMovement>> searchMovements(@ModelAttribute AssetMovementSearchCondition cond) {
        return ResponseEntity.ok(assetMovementService.search(cond));
    }

    // 5) 이동 내역 수정 - PATCH (movementId 기준)
    @PatchMapping("/{movementId}")
    public ResponseEntity<String> updateMovement(@PathVariable String movementId,
                                                 @RequestBody AssetMovementRequest request) {

        AssetMovement update = new AssetMovement();
        update.setMovementId(movementId);                // ★ 식별자
        update.setAssetId(request.getAssetId());
        update.setMovementType(request.getMovementType());
        update.setDateTakenOut(request.getDateTakenOut());
        update.setExpectedReturnDate(request.getExpectedReturnDate());
        update.setDateReturned(request.getDateReturned());
        update.setPersonTakingAsset(request.getPersonTakingAsset());
        update.setDepartment(request.getDepartment());
        update.setPurpose(request.getPurpose());
        update.setRemarks(request.getRemarks());
        update.setConditionAtCheckout(request.getConditionAtCheckout());
        update.setConditionAtCheckin(request.getConditionAtCheckin());

        int updated = assetMovementService.updateAssetMovementByMovementId(movementId, update);
        if (updated == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 movementId의 이동 내역을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("업데이트 성공");
    }

    // 6) 이동 내역 삭제 - DELETE (movementId 기준)
    @DeleteMapping("/{movementId}")
    public ResponseEntity<String> deleteMovement(@PathVariable String movementId) {
        int deleted = assetMovementService.deleteAssetMovementByMovementId(movementId);
        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 movementId의 이동 내역을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("삭제 성공");
    }
}
