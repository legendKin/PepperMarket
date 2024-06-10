package com.example.demo.service;

import com.example.demo.entity.Keyword;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service // 서비스 클래스임을 나타냄
public class BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private KeywordRepository keywordRepository;
    
//    @Value("${file.upload-dir}")
//    private String uploadDir;
    
    
    @Autowired
    public BoardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 게시글 작성 및 파일 업로드를 처리하는 메서드
    public Board write(Board board, MultipartFile file) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Users user = userRepository.findByEmail(username).orElseThrow();

        board.setUser(user);
        board.setCreateDate(LocalDateTime.now());
        
        try {
            // 파일이 비어있지 않으면 파일을 저장
            if (!file.isEmpty()) {
                String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();
                File saveFile = new File(projectPath, fileName);
                logger.info("파일 저장 경로: " + saveFile.getPath());
                file.transferTo(saveFile);
                board.setFilename(fileName);
                board.setFilepath("/files/" + fileName);
                logger.info("파일 업로드 성공: " + fileName);
            }

            Board savedBoard = boardRepository.save(board);
            return savedBoard;
        } catch (Exception e) {
            throw new Exception("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }
    // 페이징을 지원하는 모든 게시글 리스트를 가져오는 메서드
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }
    
    
    // 특정 키워드를 포함하는 게시글 리스트를 페이징하여 가져오는 메서드
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable, Integer status) {
        return boardRepository.findByTitleContainingAndStatusNot(searchKeyword, pageable, status);
    }
    
    public Page<Board> boardSearchListAll(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 ID의 게시글을 조회하는 메서드
    public Board boardView(Integer id) {
        return boardRepository.findById(id).orElseThrow();
    }

    // 특정 ID의 게시글을 삭제하는 메서드
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }
    
    // 특정 카테고리 ID의 게시글 리스트를 페이징하여 가져오는 메서드
    public Page<Board> searchByCateID(Integer searchCateID, Pageable pageable, Integer status) {
        return boardRepository.findByCateIDAndStatusNot(searchCateID, pageable, status);
    }
    
    public Page<Board> searchByCateIDAll(Integer searchCateID, Pageable pageable) {
        return boardRepository.findByCateID(searchCateID, pageable);
    }
    
    
    

    // 특정 키워드와 카테고리 ID를 포함하는 게시글 리스트를 페이징하여 가져오는 메서드
    public Page<Board> searchByKeywordAndCateID(String searchKeyword, Integer searchCateID, Pageable pageable, Integer status) {
        return boardRepository.findByTitleContainingAndCateIDAndStatusNot(searchKeyword, searchCateID, pageable, status);
    }
    
    public Page<Board> searchByKeywordAndCateIDAll(String searchKeyword, Integer searchCateID, Pageable pageable) {
        return boardRepository.findByTitleContainingAndCateID(searchKeyword, searchCateID, pageable);
    }

    // 게시글의 제목과 내용을 확인하여 키워드 알림을 생성하는 메서드
    private void checkForKeywords(Board board) {
        List<Keyword> keywords = keywordRepository.findAll();
        for (Keyword keyword : keywords) {
            if (board.getTitle().contains(keyword.getKeyword()) || board.getContent().contains(keyword.getKeyword())) {
                logger.info("Keyword matched: " + keyword.getKeyword());
                Notification notification = new Notification();
                notification.setMessage("새 게시글에 당신의 키워드 '" + keyword.getKeyword() + "'가 포함되어 있습니다.");
                notification.setUser(keyword.getUser());
                notification.setBoard(board);
                notification.setRead(false);
                notificationRepository.save(notification);
                logger.info("Notification saved for user ID: " + keyword.getUser().getId() + " for board ID: " + board.getId());
            }
        }
    }

    // 게시글을 저장하고 키워드 알림을 확인하는 메서드
    public Board saveBoard(Board board) {
        logger.info("Saving board: " + board.getTitle());
        Board savedBoard = boardRepository.save(board);
        checkForKeywords(savedBoard);
        return savedBoard;
    }
    public void boardViewCount(Integer id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewcount(board.getViewcount() + 1);
            boardRepository.save(board);
        }
    }

    // 조회수가 높은 순서로 모든 게시글을 가져오는 메서드
    public List<Board> getPostsByViewcount() {
        return boardRepository.findByOrderByViewcountDesc();
    }

    // 조회수가 높은 상위 10개의 게시글을 가져오는 메서드
    public List<Board> getTop10PostsByViewcount() {
        return boardRepository.findTop10ByOrderByViewcountDesc();
    }

    // 카테고리별 게시글 수를 가져오는 메서드
    public Map<Integer, Long> getCategoryPostCounts() {
        List<Object[]> results = boardRepository.findCategoryPostCounts();
        Map<Integer, Long> categoryPostCounts = new HashMap<>();
        for (Object[] result : results) {
            Integer cateID = (Integer) result[0];
            Long count = (Long) result[1];
            categoryPostCounts.put(cateID, count);
        }
        return categoryPostCounts;
    }
    public long getBoardCountByUserId(Long userId) {
        return boardRepository.countByUserId(userId);
    }
    
    public Page<Board> getAvailableBoard8() {
        return boardRepository.findByStatusNotOrderByCreateDateDesc(3, Pageable.ofSize(8));
    }
    
    public Page<Board> getAvailableBoard(Pageable pageable) {
        return boardRepository.findByStatusNotOrderByCreateDateDesc(3, pageable);
    }
    
    


    public void likePost(Long id) {
        boardRepository.incrementLikes(id);
    }

}
