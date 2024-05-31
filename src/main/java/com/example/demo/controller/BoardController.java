package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.Category;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Users;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.KeywordService;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class BoardController {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // 이메일을 사용
        model.addAttribute("username", email);
        return "BoardWrite";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword, Integer searchCateID) {

        Page<Board> list;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        model.addAttribute("list", list);  // 게시글 목록을 모델에 추가

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", list.getTotalPages());

        // 카테고리 관련
        List<String> categList = Category.categoryList;
        Page<Board> byCateg;

        if (searchCateID == null) {
            byCateg = boardService.boardList(pageable);  // 게시글 목록 조회
        } else {
            byCateg = boardService.searchByCateID(searchCateID, pageable);  // 검색 카테고리로 게시글 목록 조회
        }
        model.addAttribute("categList", categList);
        model.addAttribute("list", byCateg);

        return "BoardLists";
    }

    @GetMapping("/board/view/{id}")
    public String viewBoard(@PathVariable Integer id, Model model, Authentication authentication) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + id));
        model.addAttribute("board", board);

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Users> loggedInUser = userRepository.findByEmail(userDetails.getUsername()); // 이메일로 사용자 찾기
            loggedInUser.ifPresent(user -> model.addAttribute("loggedInUser", user));
        }

        return "boardView";
    }

    @GetMapping("/board/view")
    public String boardview(Model model, Integer id, Authentication authentication) {
        boardService.boardViewCount(id);
        model.addAttribute("board", boardService.boardView(id));
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        model.addAttribute("comments", comments);

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Users> loggedInUser = userRepository.findByEmail(userDetails.getUsername()); // 이메일로 사용자 찾기
            loggedInUser.ifPresent(user -> model.addAttribute("loggedInUser", user));
        }

        return "boardView";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        commentService.deleteCommentsByBoardId(id);
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        board.setViewcount(0);
        board.setCreateDate(LocalDateTime.now());
        Board savedBoard = boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료 되었습니다.");
        model.addAttribute("searchUrl", "/board/view?id=" + savedBoard.getId());

        return "message";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id);

        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setModifyDate(LocalDateTime.now());

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료 되었습니다.");
        model.addAttribute("searchUrl", "redirect:/board/view?id=" + id);

        return "message";
    }

    @PostMapping("/board/add-comment/{boardId}")
    public String addComment(@PathVariable Integer boardId, @AuthenticationPrincipal UserDetails userDetails, @RequestParam String content) {
        commentService.addComment(boardId, content, userDetails);
        return "redirect:/board/view?id=" + boardId;
    }

    // 기타 메소드들...
}
