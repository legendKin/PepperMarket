package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddUserRequest {

    @Email
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotEmpty(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordCheck;

    @NotEmpty(message = "성별을 입력해주세요")
    private String gender;

    @NotEmpty(message = "생년월일을 입력해주세요")
    private LocalDate birthDate;

    @NotBlank
    private String nickname;

}