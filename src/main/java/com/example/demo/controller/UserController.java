package com.example.demo.controller;

// 필요한 클래스와 어노테이션을 가져옴
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

// 이 클래스가 스프링 컨트롤러임을 나타내고, @RequiredArgsConstructor를 사용하여 final 필드에 대한 생성자를 자동 생성
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService; // UserService 주입을 위한 필드 선언

    // 회원가입 요청을 처리
    @PostMapping("/user")
    public String signup(@Valid AddUserRequest request, BindingResult bindingResult) {
        // 입력 값 검증에서 오류가 발생하면 회원가입 폼으로 돌아감
        if (bindingResult.hasErrors()) {
            return "signup"; // 회원가입 폼 뷰 반환
        }

        // 패스워드와 패스워드 확인 값이 일치하지 않으면 오류 처리
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup"; // 회원가입 폼 뷰 반환
        }

        try {
            userService.save(request); // 사용자 저장
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다."); // 중복 사용자 오류 처리
            return "signup"; // 회원가입 폼 뷰 반환
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage()); // 기타 오류 처리
            return "signup"; // 회원가입 폼 뷰 반환
        }

        return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로 리디렉션
    }

    // 로그인 페이지를 표시
    @GetMapping("/login")
    public String login() {
        return "login"; // 로그인 뷰 반환
    }

    // 회원가입 폼을 표시
    @GetMapping("/signup")
    public String signup(AddUserRequest addUserRequest) {
        return "signup"; // 회원가입 폼 뷰 반환
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login"; // 로그아웃 후 로그인 페이지로 리디렉션
    }
}
