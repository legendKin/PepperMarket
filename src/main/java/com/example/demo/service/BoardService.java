package com.example.demo.service;

import com.example.demo.entity.Keyword;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
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

        try {
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
            logger.info("게시글 저장 성공: " + savedBoard.getId());
            return savedBoard;
        } catch (Exception e) {
            logger.error("파일 업로드 중 오류 발생", e);
            throw new Exception("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    public Board boardView(Integer id) {
        return boardRepository.findById(id).orElseThrow();
    }

    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    public Page<Board> searchByCateID(Integer searchCateID, Pageable pageable) {
        return boardRepository.findByCateID(searchCateID, pageable);
    }

    public Page<Board> searchByKeywordAndCateID(String searchKeyword, Integer searchCateID, Pageable pageable) {
        return boardRepository.findByTitleContainingAndCateID(searchKeyword, searchCateID, pageable);
    }

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

    public Board saveBoard(Board board) {
        logger.info("Saving board: " + board.getTitle());
        Board savedBoard = boardRepository.save(board);
        checkForKeywords(savedBoard);
        return savedBoard;
    }

    public List<Board> getPostsByViewcount() {
        return boardRepository.findByOrderByViewcountDesc();
    }

    public List<Board> getTop10PostsByViewcount() {
        return boardRepository.findTop10ByOrderByViewcountDesc();
    }

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
}
