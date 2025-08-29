package test.jytest.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 추가 - POST
    @PostMapping
    public ResponseEntity<Member> create(@RequestBody final MemberRequest request) {
        Member member = new Member(
                request.getCustomerId(),
                request.getCustomerType(),
                request.getCustomerName()
        );
        return ResponseEntity.ok(memberService.createMember(member));
    }

    // 전체 회원 조회 - GET
    @GetMapping
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(memberService.findMembers());
    }

    // customer_id 로 회원 조회 - GET
    @GetMapping("/{customer_id}")
    public ResponseEntity<Member> getMember(@PathVariable("customer_id") final String customerId) {
        Optional<Member> memberOpt = memberService.findMemberById(customerId);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(memberOpt.get());
    }

    // 검색 조건으로 검색 - GET
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMember(final MemberSearchCondition cond) {
        return ResponseEntity.ok(memberService.search(cond));
    }

    // 회원 정보 수정 - PATCH
    @PatchMapping("/{customer_id}")
    public ResponseEntity<String> updateMember(@PathVariable("customer_id") final String customerId,
                                               @RequestBody final MemberRequest request) {
        Member updateMember = new Member();
        updateMember.setCustomerId(customerId);
        updateMember.setCustomerType(request.getCustomerType());
        updateMember.setCustomerName(request.getCustomerName());

        int updatedCount = memberService.updateMember(updateMember);
        if (updatedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 회원을 찾을 수 없거나 수정된 데이터가 없습니다.");
        }
        return ResponseEntity.ok("업데이트 성공");
    }

    // 회원 삭제 - DELETE
    @DeleteMapping("/{customer_id}")
    public ResponseEntity<String> removeMember(@PathVariable("customer_id") final String customerId) {
        int deletedCount = memberService.deleteMember(customerId);
        if (deletedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 회원을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("회원 삭제 성공");
    }
}
