package test.jytest.assetMovement;

import org.springframework.stereotype.Service;
import test.jytest.repository.AssetMovementRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AssetMovementService {

    private final AssetMovementRepository assetMovementRepository;

    public AssetMovementService(AssetMovementRepository assetMovementRepository) {
        this.assetMovementRepository = assetMovementRepository;
    }

    /** 자산 이동 생성 */
    public AssetMovement createAssetMovement(final AssetMovement assetMovement) {
        return assetMovementRepository.save(assetMovement);
    }

    /** 전체 자산 이동 조회 */
    public List<AssetMovement> findAllAssetMovement() {
        return assetMovementRepository.findAll();
    }

    /** movementId(시리얼 넘버)로 단건 조회 */
    public Optional<AssetMovement> findAssetMovementByMovementId(final String movementId) {
        return assetMovementRepository.findByMovementId(movementId);
    }

    /** movementId 기준 업데이트 */
    public int updateAssetMovementByMovementId(final String movementId, final AssetMovement update) {
        return assetMovementRepository.updateByMovementId(movementId, update);
    }

    /** movementId 기준 삭제 */
    public int deleteAssetMovementByMovementId(final String movementId) {
        return assetMovementRepository.deleteByMovementId(movementId);
    }

    /** 검색 */
    public List<AssetMovement> search(final AssetMovementSearchCondition cond) {
        return assetMovementRepository.search(cond);
    }
}
