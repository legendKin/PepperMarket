package com.example.demo.entity;

import java.time.LocalDateTime;
import com.example.demo.domain.Users;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    private String filename;
    private String filepath;
    private Integer viewcount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "createdate", nullable = true)
    private LocalDateTime createDate;

    @Column(name = "modifydate", nullable = true)
    private LocalDateTime modifyDate;
}
