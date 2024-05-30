package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findByCateID(Integer searchCateID, Pageable pageable);
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
    void deleteById(Long id);

//    Page<Board> findByCateID(Integer searchCateID, Pageable pageable);
}