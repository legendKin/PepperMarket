package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Users;
import com.example.demo.entity.ViewedPost;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.*;

import jakarta.servlet.http.HttpSession;
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
import java.util.Optional;

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

    @Autowired
    private ViewedPostService viewedPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public BoardController(BoardService boardService, CommentService commentService, NotificationService notificationService) {
        this.boardService = boardService;
        this.commentService = commentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        model.addAttribute("username", email);
        return "boardWrite";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword, Integer searchCateID) {
        logger.info("boardList method called");
        Page<Board> list;

        if (searchKeyword != null && searchCateID != null) {
            list = boardService.searchByKeywordAndCateID(searchKeyword, searchCateID, pageable, 3);
        } else if (searchKeyword != null) {
            list = boardService.boardSearchList(searchKeyword, pageable, 3);
        } else if (searchCateID != null) {
            list = boardService.searchByCateID(searchCateID, pageable, 3);
        } else {
            list = boardService.getAvailableBoard(pageable);
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


        Long loggedUserId = getCurrentUserId();
        model.addAttribute("loggedUserId", loggedUserId);

        logger.info("Rendering boardList template");
        return "boardLists";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, Integer id, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        logger.info("boardView 호출됨: id={}, userDetails={}", id, userDetails);

        // 세션에서 좋아요 요청 플래그 확인
        Boolean likeRequest = (Boolean) session.getAttribute("likeRequest");
        if (likeRequest == null || !likeRequest) {
            boardService.boardViewCount(id); // 조회수 증가 로직
        } else {
            session.removeAttribute("likeRequest"); // 플래그 초기화
        }

        Board board = boardService.boardView(id);
        model.addAttribute("board", board);
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        model.addAttribute("comments", comments);

        if (userDetails != null) {
            Users currentUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            model.addAttribute("loggedInUser", currentUser);

            // 사용자 좋아요 여부 추가
            boolean liked = likeService.hasUserLiked(id.longValue(), userDetails.getUsername());
            model.addAttribute("liked", liked);
        } else {
            model.addAttribute("liked", false);
        }

        Long userId = getCurrentUserId();
        viewedPostService.addViewedPost(userId, Long.valueOf(id));

        viewedPostService.addViewedPost(userId, id.longValue());

        String writer = board.getUser().getName();
        model.addAttribute("writer", writer);
        String writerPic = board.getUser().getProfilePictureUrl();
        model.addAttribute("writerPic", writerPic);

        Long userPostCount = boardService.getBoardCountByUserId(board.getUser().getId());
        model.addAttribute("userPostCount", userPostCount);
        // 좋아요 수 추가
        long likeCount = board.getLikecount() == null ? 0 : board.getLikecount();
        model.addAttribute("likeCount", likeCount);

        return "boardView";




    }



    private Long getCurrentUserId() {
        // 실제 사용자 ID를 가져오는 로직으로 대체해야 합니다.
        return 1L; // 예시로 1을 반환
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

            model.addAttribute("message", "게시글 작성 완료했습니다.");
            model.addAttribute("redirectUrl", "/board/view?id=" + savedBoard.getId());
            return "message";
        } catch (Exception e) {
            logger.error("게시글 작성 중 오류 발생", e);
            model.addAttribute("message", "게시글 작성 중 오류가 발생했습니다.");
            model.addAttribute("redirectUrl", "/board/write");
            return "message";
        }
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id);

        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setModifyDate(LocalDateTime.now());
        boardTemp.setStatus(board.getStatus());

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("redirectUrl", "/board/view?id=" + id);
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

    @GetMapping("/board/delete/{id}")
    public String boardDelete(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Board board = boardService.boardView(id);
        Users currentUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        if (currentUser == null || (!board.getUser().getEmail().equals(userDetails.getUsername()) && !"admin".equals(currentUser.getNickname()))) {
            model.addAttribute("message", "작성자 또는 관리자로 로그인한 경우에만 글을 삭제할 수 있습니다.");
            model.addAttribute("redirectUrl", "/board/list");
            return "message";
        }

        notificationService.deleteNotificationsByBoardId(id);
        commentService.deleteCommentsByBoardId(id);
        boardService.boardDelete(id);

        model.addAttribute("message", "게시글 삭제 완료했습니다.");
        model.addAttribute("redirectUrl", "/board/list");
        return "message";
    }

    @GetMapping("/board/recentViewedPosts")
    public String recentViewedPosts(Model model) {
        Long userId = getCurrentUserId();
        List<ViewedPost> viewedPosts = viewedPostService.getRecentViewedPosts(userId);
        model.addAttribute("viewedPosts", viewedPosts);
        return "recentViewedPosts";
    }
    @Autowired
    private LikeService likeService;

    @PostMapping("/board/like")
    public String likeBoard(@RequestParam Integer boardId, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("likeBoard 호출됨: boardId={}, userDetails={}", boardId, userDetails);
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        String userEmail = userDetails.getUsername();
        boolean liked = likeService.addLike(boardId.longValue(), userEmail);

        // 좋아요 상태 메시지 추가
        redirectAttributes.addFlashAttribute("message", liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");

        // 좋아요 요청 플래그 설정
        session.setAttribute("likeRequest", true);

        return "redirect:/board/view?id=" + boardId;
    }
    @GetMapping("/board/like/count")
    @ResponseBody
    public Long getLikeCount(@RequestParam Integer boardId) {
        logger.info("getLikeCount 호출됨: boardId={}", boardId);
        return likeService.getLikeCount(boardId.longValue());
    }

}


