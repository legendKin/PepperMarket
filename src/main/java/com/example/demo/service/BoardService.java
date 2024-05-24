package com.example.demo.service;


import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository; // 게시글 레포지토리 주입
    private UserRepository userRepository; // 사용자 레포지토리 주입

    @Autowired
    public BoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 새로운 게시글 작성
    @Transactional(rollbackFor = Exception.class)
    public void write(Board board, MultipartFile file) throws Exception {
        // 현재 로그인한 사용자의 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Users user = userRepository.findByEmail(username).orElseThrow();

        // 게시글 작성자 설정
        board.setUser(user);
        board.setCreateDate(LocalDateTime.now());

        // 파일이 비어있는지 확인하고 처리
        if (file.isEmpty()) {
            board.setFilename(board.getFilename());
            board.setFilepath(board.getFilepath());
        } else {
            // 파일이 비어있지 않은 경우, 파일 저장 및 정보 설정
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        // 게시글 저장
        boardRepository.save(board);
    }

    // 게시글 목록 가져오기
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 특정 게시글 조회
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

    // 특정 게시글 삭제
    @Transactional(rollbackFor = Exception.class)
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    // 특정 게시글 조회수 증가
    @Transactional(rollbackFor = Exception.class)
    public void boardViewCount(Integer id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewcount(board.getViewcount() + 1);
            boardRepository.save(board);
        }
    }

    // 검색 키워드로 게시글 목록 가져오기
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }
}
