package test.jytest.repository;

import test.jytest.assetMovement.AssetMovement;
import test.jytest.assetMovement.AssetMovementSearchCondition;

import java.util.List;
import java.util.Optional;

public interface AssetMovementRepository {

    /** 저장 */
    AssetMovement save(AssetMovement assetMovement);

    Optional<AssetMovement> findById(String movementId);

    /** movementId(시리얼 넘버)로 조회 */
    Optional<AssetMovement> findByMovementId(String movementId);

    /** 전체 조회 */
    List<AssetMovement> findAll();

    int update(AssetMovement assetMovement);

    int delete(String movementId);

    Optional<AssetMovement> findById(Long movementId);

    /** 검색 조건 조회 */
    List<AssetMovement> search(AssetMovementSearchCondition cond);

    int delete(Long movementId);

    /** movementId 기준 업데이트 */
    int updateByMovementId(String movementId, AssetMovement assetMovement);

    /** movementId 기준 삭제 */
    int deleteByMovementId(String movementId);
}
