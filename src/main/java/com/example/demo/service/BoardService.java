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

/**
 * 게시글과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository; // 게시글 레포지토리
    private UserRepository userRepository; // 사용자 레포지토리

    @Autowired
    public BoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 새로운 게시글을 작성하고 저장하는 메서드입니다.
     * @param board 새로 작성된 게시글
     * @param file 업로드된 파일
     * @throws Exception 파일 업로드 과정에서 예외가 발생할 수 있습니다.
     */
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

    /**
     * 페이지네이션을 적용하여 전체 게시글 목록을 반환하는 메서드입니다.
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 게시글 목록
     */
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    /**
     * 특정 ID에 해당하는 게시글을 조회하는 메서드입니다.
     * @param id 조회할 게시글의 ID
     * @return 조회된 게시글
     * @throws NoSuchElementException 해당 ID에 해당하는 게시글이 없을 경우 예외가 발생합니다.
     */
    public Board boardView(Integer id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            return boardOptional.get();
        } else {
            throw new NoSuchElementException("Board not found with id: " + id);
        }
    }

    /**
     * 특정 ID에 해당하는 게시글을 삭제하는 메서드입니다.
     * @param id 삭제할 게시글의 ID
     */
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    /**
     * 특정 ID에 해당하는 게시글의 조회수를 증가시키는 메서드입니다.
     * @param id 조회수를 증가시킬 게시글의 ID
     */
    public void boardViewCount(Integer id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewcount(board.getViewcount() + 1);
            boardRepository.save(board);
        }
    }

    /**
     * 제목에 검색 키워드를 포함하는 게시글 목록을 반환하는 메서드입니다.
     * @param searchKeyword 검색 키워드
     * @param pageable 페이지네이션 정보
     * @return 검색 결과에 해당하는 게시글 목록
     */
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }
}
