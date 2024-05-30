package com.example.demo.controller;

import com.example.demo.domain.Keyword;
import com.example.demo.domain.PrincipalDetails;
import com.example.demo.domain.Users;
import com.example.demo.dto.KeywordRequest;
import com.example.demo.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keywords")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @PostMapping
    public ResponseEntity<Keyword> addKeyword(@RequestBody KeywordRequest keywordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Users user = principalDetails.getUsers();
        Keyword keyword = new Keyword();
        keyword.setKeyword(keywordRequest.getKeyword());
        keyword.setUser(user);
        return ResponseEntity.ok(keywordService.save(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        keywordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Keyword>> getKeywords() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Users user = principalDetails.getUsers();
        return ResponseEntity.ok(keywordService.findByUser(user));
    }
}
