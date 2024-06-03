package com.example.demo.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 파일 업로드 크기 제한 초과 예외 처리
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "File too large!");
        return "error";
    }
    // 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public String handleException(Exception exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "An error occurred: " + exc.getMessage());
        return "error";
    }
}
