package com.example.demo.service;

import com.example.demo.domain.Keyword;
import com.example.demo.domain.Users;
import com.example.demo.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordService {

    @Autowired
    private KeywordRepository keywordRepository;

    public Keyword save(Keyword keyword) {
        return keywordRepository.save(keyword);
    }

    public void delete(Long id) {
        keywordRepository.deleteById(id);
    }

    public List<Keyword> findAll() {
        return keywordRepository.findAll();
    }

    public List<Keyword> findByUser(Users user) {
        return keywordRepository.findByUser(user);
    }
}