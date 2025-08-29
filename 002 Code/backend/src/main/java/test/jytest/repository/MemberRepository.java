package test.jytest.repository;

import test.jytest.member.Member;
import test.jytest.member.MemberSearchCondition;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    /** 새로운 멤버 저장 */
    Member save(Member member);

    /** 전체 멤버 조회 */
    List<Member> findAll();

    /** customerId 기준 단건 조회 */
    Optional<Member> findByCustomerId(String customerId);

    /** 검색 조건 기반 조회 */
    List<Member> search(MemberSearchCondition cond);

    /** customerId 기준 업데이트 */
    int updateByCustomerId(String customerId, Member update);

    /** customerId 기준 삭제 */
    int deleteByCustomerId(String customerId);
}
