package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    // 카테고리별로 게시글을 구분하기 위해 필요합니다.
    public Category(String name) {
        this.name = name;
    }

    // 기본 생성자
    public Category() {}
}