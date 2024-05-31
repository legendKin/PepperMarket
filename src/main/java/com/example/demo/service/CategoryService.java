package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    public static final List<String> categoryList = Arrays.asList(
            "카테고리1", "카테고리2", "카테고리3", "카테고리4",
            "카테고리5", "카테고리6", "카테고리7", "카테고리8"
    );

    public List<String> getCategoryList() {
        return categoryList;
    }
}
