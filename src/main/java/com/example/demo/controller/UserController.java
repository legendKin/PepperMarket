package com.example.demo.controller;

import com.example.demo.dto.AddUserRequest;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor // Lombok을 사용하여 필요한 생성자를 자동으로 생성
@Controller // Spring MVC에서 컨트롤러로 작동하도록 지정
public class UserController {

    private final UserService userService; // UserService를 주입받아 사용

    // 회원가입 요청을 처리하는 메서드
    @PostMapping("/user")
    public String signup(@Valid AddUserRequest request, BindingResult bindingResult) {
        // 유효성 검사에서 오류가 발생하면 회원가입 페이지로 이동
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // 비밀번호와 비밀번호 확인이 일치하지 않으면 오류 추가 후 회원가입 페이지로 이동
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup";
        }

        try {
            // 사용자 정보를 저장
            userService.save(request);
        } catch (DataIntegrityViolationException e) {
            // 데이터 무결성 위반 예외가 발생하면 (이미 존재하는 사용자) 오류 추가 후 회원가입 페이지로 이동
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch (IllegalArgumentException e) {
            // 닉네임이 'admin'일 경우 예외 처리
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        } catch (Exception e) {
            // 그 외 예외가 발생하면 오류 메시지 추가 후 회원가입 페이지로 이동
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }

        // 회원가입이 성공하면 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }

    // 로그인 페이지 요청을 처리하는 메서드
    @GetMapping("/login")
    public String login() {
        // "login" 뷰를 반환 (resources/templates/login.html 파일을 가리킴)
        return "login";
    }

    // 회원가입 페이지 요청을 처리하는 메서드
    @GetMapping("/signup")
    public String signup(AddUserRequest addUserRequest) {
        // "signup" 뷰를 반환 (resources/templates/signup.html 파일을 가리킴)
        return "signup";
    }

    // 로그아웃 요청을 처리하는 메서드
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // SecurityContextLogoutHandler를 사용하여 로그아웃 처리
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        // 로그아웃 후 홈 페이지로 리다이렉트
        return "redirect:/";
    }
}
