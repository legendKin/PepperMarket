package com.example.demo.service;

import com.example.demo.domain.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    @Autowired
    public BoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void write(Board board, MultipartFile file) throws Exception {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Users user = userRepository.findByEmail(username).orElseThrow();

        board.setUser(user);  // 작성자 설정
        board.setCreateDate(LocalDateTime.now());
        if (file.isEmpty()) {
            board.setFilename(board.getFilename());
            board.setFilepath(board.getFilepath());
        } else {

            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

            UUID uuid = UUID.randomUUID();

            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);

            file.transferTo(saveFile);

            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board);

    }


    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board boardView(Integer id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        // Optional에서 값이 있는지 확인
        if (boardOptional.isPresent()) {
            // 값이 있는 경우에만 get 메서드 호출
            return boardOptional.get();
        } else {
            // 값이 없는 경우에는 예외 처리 또는 기본값 반환
            throw new NoSuchElementException("Board not found with id: " + id);
        }
    }

    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }


    public void boardViewCount(Integer id) {

        Board board = boardRepository.findById(id).orElse(null);

        if (board != null) {
            board.setViewcount(board.getViewcount() + 1);
            boardRepository.save(board);
        }
    }


    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

}