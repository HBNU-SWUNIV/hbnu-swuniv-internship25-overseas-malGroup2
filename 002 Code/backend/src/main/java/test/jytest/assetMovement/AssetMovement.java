package test.jytest.assetMovement;

import java.sql.Date;
import java.util.Objects;

public class AssetMovement {

    /** 식별자: 시리얼 넘버(문자열) */
    private String movementId;

    /** 자산 ID (FK) */
    private String assetId;

    /** 이동 유형 */
    private String movementType;

    /** 대여일/반출일 */
    private Date dateTakenOut;

    /** 예상 반납일 */
    private Date expectedReturnDate;

    /** 실제 반납일 */
    private Date dateReturned;

    /** 자산 수령자 */
    private String personTakingAsset;

    /** 소속 부서 */
    private String department;

    /** 사용 목적 */
    private String purpose;

    /** 비고 */
    private String remarks;

    /** 출고 시 상태 */
    private String conditionAtCheckout;

    /** 반납 시 상태 */
    private String conditionAtCheckin;

    public AssetMovement() {}

    /** 전체 필드 생성자 (식별자 포함) */
    public AssetMovement(String movementId, String assetId, String movementType, Date dateTakenOut,
                         Date expectedReturnDate, Date dateReturned, String personTakingAsset,
                         String department, String purpose, String remarks,
                         String conditionAtCheckout, String conditionAtCheckin) {
        this.movementId = movementId;               // ★ 시리얼 넘버 문자열
        this.assetId = assetId;
        this.movementType = movementType;
        this.dateTakenOut = dateTakenOut;
        this.expectedReturnDate = expectedReturnDate;
        this.dateReturned = dateReturned;
        this.personTakingAsset = personTakingAsset;
        this.department = department;
        this.purpose = purpose;
        this.remarks = remarks;
        this.conditionAtCheckout = conditionAtCheckout;
        this.conditionAtCheckin = conditionAtCheckin;
    }

    /** 식별자 없이 생성 (필요 시) */
    public AssetMovement(String assetId, String movementType, Date dateTakenOut,
                         Date expectedReturnDate, Date dateReturned, String personTakingAsset,
                         String department, String purpose, String remarks,
                         String conditionAtCheckout, String conditionAtCheckin) {
        this(null, assetId, movementType, dateTakenOut, expectedReturnDate, dateReturned,
                personTakingAsset, department, purpose, remarks, conditionAtCheckout, conditionAtCheckin);
    }

    // === getters / setters ===

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Date getDateTakenOut() {
        return dateTakenOut;
    }

    public void setDateTakenOut(Date dateTakenOut) {
        this.dateTakenOut = dateTakenOut;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Date getDateReturned() {
        return dateReturned;
    }

    public void setDateReturned(Date dateReturned) {
        this.dateReturned = dateReturned;
    }

    public String getPersonTakingAsset() {
        return personTakingAsset;
    }

    public void setPersonTakingAsset(String personTakingAsset) {
        this.personTakingAsset = personTakingAsset;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getConditionAtCheckout() {
        return conditionAtCheckout;
    }

    public void setConditionAtCheckout(String conditionAtCheckout) {
        this.conditionAtCheckout = conditionAtCheckout;
    }

    public String getConditionAtCheckin() {
        return conditionAtCheckin;
    }

    public void setConditionAtCheckin(String conditionAtCheckin) {
        this.conditionAtCheckin = conditionAtCheckin;
    }

    // === object methods ===

    @Override
    public String toString() {
        return "AssetMovement{" +
                "movementId='" + movementId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", movementType='" + movementType + '\'' +
                ", dateTakenOut=" + dateTakenOut +
                ", expectedReturnDate=" + expectedReturnDate +
                ", dateReturned=" + dateReturned +
                ", personTakingAsset='" + personTakingAsset + '\'' +
                ", department='" + department + '\'' +
                ", purpose='" + purpose + '\'' +
                ", remarks='" + remarks + '\'' +
                ", conditionAtCheckout='" + conditionAtCheckout + '\'' +
                ", conditionAtCheckin='" + conditionAtCheckin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AssetMovement that = (AssetMovement) o;
        return Objects.equals(movementId, that.movementId) &&
                Objects.equals(assetId, that.assetId) &&
                Objects.equals(movementType, that.movementType) &&
                Objects.equals(dateTakenOut, that.dateTakenOut) &&
                Objects.equals(expectedReturnDate, that.expectedReturnDate) &&
                Objects.equals(dateReturned, that.dateReturned) &&
                Objects.equals(personTakingAsset, that.personTakingAsset) &&
                Objects.equals(department, that.department) &&
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(remarks, that.remarks) &&
                Objects.equals(conditionAtCheckout, that.conditionAtCheckout) &&
                Objects.equals(conditionAtCheckin, that.conditionAtCheckin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movementId, assetId, movementType, dateTakenOut,
                expectedReturnDate, dateReturned, personTakingAsset, department,
                purpose, remarks, conditionAtCheckout, conditionAtCheckin);
    }
}
