package com.lv2dev.echonet.controller;

import com.lv2dev.echonet.dto.MemberDTO;
import com.lv2dev.echonet.model.Member;
import com.lv2dev.echonet.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.signUp(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Member registration successful");
        } catch (ResponseStatusException e) {
            // 여기서 ResponseStatusException을 catch하고, 이를 바탕으로 적절한 응답을 반환합니다.
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     *
     * @param email        비밀번호를 변경하려는 사용자의 이메일 주소입니다.
     * @param currentPassword 사용자의 현재 비밀번호입니다.
     * @param newPassword  사용자가 설정할 새로운 비밀번호입니다.
     * @return ResponseEntity 변경 성공 메시지를 담은 HTTP 응답입니다.
     * @throws ResponseStatusException 현재 비밀번호가 올바르지 않거나, 기타 오류 발생 시 예외를 발생시킵니다.
     */
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestParam String currentPassword, @RequestParam String newPassword) {
        Member member = memberService.findMemberByEmail(email);
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        memberService.resetPassword(email, newPassword);
        return ResponseEntity.ok().body("Password changed successfully");
    }
}