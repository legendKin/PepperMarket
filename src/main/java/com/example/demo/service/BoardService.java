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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public BoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Board write(Board board, MultipartFile file) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Users user = userRepository.findByEmail(username).orElseThrow();

        board.setUser(user);
        board.setCreateDate(LocalDateTime.now());
        if (!file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        return boardRepository.save(board);
    }

    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board boardView(Integer id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            return boardOptional.get();
        } else {
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
