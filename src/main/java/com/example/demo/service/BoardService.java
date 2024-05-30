package com.example.demo.service;

import com.example.demo.entity.Category;
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
import java.util.List;
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
    private NotificationRepository notificationRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
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
        if (!file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }
        Board savedBoard = boardRepository.save(board);
        checkForKeywords(savedBoard);  // 키워드 체크 및 알림 생성 호출
        return savedBoard;

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

    public Page<Board> searchByCateID(Integer searchCateID, Pageable pageable) {
        return boardRepository.findByCateID(searchCateID, pageable);
    }

    private void checkForKeywords(Board board) {
        List<Keyword> keywords = keywordRepository.findAll();
        for (Keyword keyword : keywords) {
            if (board.getTitle().contains(keyword.getKeyword()) || board.getContent().contains(keyword.getKeyword())) {
                System.out.println("Keyword matched: " + keyword.getKeyword()); // 로그 추가
                Notification notification = new Notification();
                notification.setMessage("새 게시글에 당신의 키워드 '" + keyword.getKeyword() + "'가 포함되어 있습니다.");
                notification.setUser(keyword.getUser());
                notification.setBoard(board);  // 게시글 정보 추가
                notification.setRead(false);
                notificationRepository.save(notification);
                System.out.println("Notification saved for user ID: " + keyword.getUser().getId() + " for board ID: " + board.getId()); // 로그 추가
            }
        }
    }

//    public List<Board> boardListByCategory(Board cateID) {
//        Board category = boardRepository.findByCategoryIDX(cateID)
//                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + cateID));
//        return boardRepository.findByCategoryIDX(category);
//    }
    public Board saveBoard(Board board) {
        System.out.println("Saving board: " + board.getTitle()); // 로그 추가
        Board savedBoard = boardRepository.save(board);
        checkForKeywords(savedBoard);  // 키워드 체크 및 알림 생성 호출
        return savedBoard;
    }
}