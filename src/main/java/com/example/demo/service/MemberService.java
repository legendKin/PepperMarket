package com.example.demo.service;

import com.example.demo.domain.Users;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Users findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
