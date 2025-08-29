package test.jytest.member;

import java.util.Objects;


public class Member {

    private String customerId;
    private String customerType;
    private String customerName;

    public Member() {}

    public Member(String customerId, String customerType, String customerName) {
        this.customerId = customerId;
        this.customerType = customerType;
        this.customerName = customerName;
    }

    /** customerId 없이 생성(필요 시 사용) */
    public Member(String customerType, String customerName) {
        this.customerType = customerType;
        this.customerName = customerName;
    }

    // === getters / setters ===

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

    // === object methods ===

    @Override
    public String toString() {
        return "Member{" +
                "customerId='" + customerId + '\'' +
                ", customerType='" + customerType + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(customerId, member.customerId) &&
                Objects.equals(customerType, member.customerType) &&
                Objects.equals(customerName, member.customerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerType, customerName);
    }
}
