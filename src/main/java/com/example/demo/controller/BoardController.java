package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.service.BoardService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.CommentService;
import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // 이메일을 사용
        model.addAttribute("username", email);
        return "BoardWrite";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword, Integer searchCateID) {
        logger.info("boardList method called");
        Page<Board> list;

        if (searchKeyword != null && searchCateID != null) {
            list = boardService.searchByKeywordAndCateID(searchKeyword, searchCateID, pageable);
        } else if (searchKeyword != null) {
            list = boardService.boardSearchList(searchKeyword, pageable);
        } else if (searchCateID != null) {
            list = boardService.searchByCateID(searchCateID, pageable);
        } else {
            list = boardService.boardList(pageable);
        }

        model.addAttribute("list", list);
        logger.info("List size: " + list.getTotalElements());

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", list.getTotalPages());

        List<String> categList = categoryService.getCategoryList();
        Map<Integer, Long> categoryPostCounts = boardService.getCategoryPostCounts();
        String categNow;
        if (searchCateID == null) {
            categNow = "전체 상품";
        } else {
            categNow = categList.get(searchCateID - 1);
        }
        model.addAttribute("categNow", categNow);
        model.addAttribute("categList", categList);
        model.addAttribute("categoryPostCounts", categoryPostCounts);

        logger.info("Rendering boardList template");
        return "boardLists";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        Board board = boardService.boardView(id);
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("loggedInUser", userDetails); // 로그인한 사용자 정보 추가
        return "boardView";
    }


    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        logger.info("게시글 작성 시작");

        board.setViewcount(0);
        board.setCreateDate(LocalDateTime.now());

        try {
            Board savedBoard = boardService.write(board, file);
            logger.info("게시글 저장 완료: " + savedBoard.getId());

            notificationService.notify(savedBoard);
            logger.info("알림 생성 완료");

            model.addAttribute("message", "글 작성이 완료 되었습니다.");
            model.addAttribute("searchUrl", "/board/view?id=" + savedBoard.getId());

            logger.info("메시지 페이지로 이동 준비");

            return "message";
        } catch (Exception e) {
            logger.error("게시글 작성 중 오류 발생", e);
            model.addAttribute("message", "게시글 작성 중 오류가 발생했습니다.");
            model.addAttribute("searchUrl", "/board/write");
            return "message";
        }
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


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        logger.warn("파일 업로드 크기 초과", e);
        redirectAttributes.addFlashAttribute("message", "파일 업로드 크기를 초과했습니다. 최대 파일 크기는 10MB입니다.");
        return "redirect:/board/write";
    }
}
