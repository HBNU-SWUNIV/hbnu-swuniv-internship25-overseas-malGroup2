package test.jytest.member;

import org.springframework.stereotype.Service;
import test.jytest.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** 회원 생성 */
    public Member createMember(final Member member) {
        return memberRepository.save(member);
    }

    /** 전체 회원 조회 */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /** customerId로 단건 조회 */
    public Optional<Member> findMemberById(final String customerId) {
        return memberRepository.findByCustomerId(customerId);
    }

    /** 회원 정보 수정 (customerId 기준) */
    public int updateMember(final Member update) {
        return memberRepository.updateByCustomerId(update.getCustomerId(), update);
    }

    /** 회원 삭제 (customerId 기준) */
    public int deleteMember(final String customerId) {
        return memberRepository.deleteByCustomerId(customerId);
    }

    /** 검색 */
    public List<Member> search(final MemberSearchCondition cond) {
        return memberRepository.search(cond);
    }
}
