package com.example.demo.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// 사용자 등록 요청을 담는 DTO 클래스
@Getter // lombok을 사용하여 getter 메서드 자동 생성
@Setter // lombok을 사용하여 setter 메서드 자동 생성
public class AddUserRequest {

    // 이메일을 검증하는 어노테이션과 메시지 설정
    @Email
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    // 비밀번호를 검증하는 어노테이션과 메시지 설정
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    // 비밀번호 확인을 검증하는 어노테이션과 메시지 설정
    @NotEmpty(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordCheck;

    // 성별을 검증하는 어노테이션과 메시지 설정
    @NotEmpty(message = "성별을 입력해주세요")
    private String gender;

    // 생년월일을 검증하는 어노테이션과 메시지 설정
    @DateTimeFormat(pattern = "yyyyMMdd")
    @NotEmpty(message = "생년월일을 입력해주세요")
    private LocalDate birthDate;

    // 닉네임을 검증하는 어노테이션과 메시지 설정
    @NotBlank
    private String nickname;

}
