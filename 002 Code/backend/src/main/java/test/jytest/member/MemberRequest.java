package test.jytest.member;

public class MemberRequest {

    /** 회원 ID (PK) */
    private String customerId;

    /** 회원 유형: custodian, asset manager, requester, store manager 등 */
    private String customerType;

    /** 회원 이름 */
    private String customerName;

    public MemberRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
