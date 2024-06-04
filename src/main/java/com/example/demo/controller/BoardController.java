package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
// 수정된 부분: import 구문 추가
import com.example.demo.entity.Users;
import com.example.demo.entity.ViewedPost;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.*;

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

    // 수정된 부분: ViewedPostService 빈 주입
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

    // 게시글 작성 폼을 보여주는 메서드
    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername(); // 로그인된 사용자의 이메일을 가져옴
        model.addAttribute("username", email); // 이메일을 모델에 추가하여 View에서 사용

        return "BoardWrite"; // 게시글 작성 페이지로 이동
    }

    // 게시글 리스트를 보여주는 메서드
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword, Integer searchCateID) {
        logger.info("boardList method called");
        Page<Board> list;

        // 검색어와 카테고리 ID가 모두 있는 경우
        if (searchKeyword != null && searchCateID != null) {
            list = boardService.searchByKeywordAndCateID(searchKeyword, searchCateID, pageable);
        } else if (searchKeyword != null) { // 검색어만 있는 경우
            list = boardService.boardSearchList(searchKeyword, pageable);
        } else if (searchCateID != null) { // 카테고리 ID만 있는 경우
            list = boardService.searchByCateID(searchCateID, pageable);
        } else { // 검색어와 카테고리 ID가 모두 없는 경우
            list = boardService.boardList(pageable);
        }

        model.addAttribute("list", list); // 게시글 리스트를 모델에 추가
        logger.info("List size: " + list.getTotalElements());

        // 페이지 정보 계산
        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("nowPage", nowPage); // 현재 페이지 번호를 모델에 추가
        model.addAttribute("startPage", startPage); // 시작 페이지 번호를 모델에 추가
        model.addAttribute("endPage", endPage); // 끝 페이지 번호를 모델에 추가
        model.addAttribute("totalPage", list.getTotalPages()); // 총 페이지 수를 모델에 추가

        List<String> categList = categoryService.getCategoryList(); // 카테고리 리스트를 가져옴
        Map<Integer, Long> categoryPostCounts = boardService.getCategoryPostCounts(); // 각 카테고리별 게시글 수를 가져옴
        String categNow;
        if (searchCateID == null) {
            categNow = "전체 상품"; // 검색 카테고리가 없으면 "전체 상품"으로 설정
        } else {
            categNow = categList.get(searchCateID - 1); // 카테고리 ID에 해당하는 카테고리 이름을 설정
        }
        model.addAttribute("categNow", categNow); // 현재 카테고리를 모델에 추가
        model.addAttribute("categList", categList); // 카테고리 리스트를 모델에 추가
        model.addAttribute("categoryPostCounts", categoryPostCounts); // 카테고리별 게시글 수를 모델에 추가

        logger.info("Rendering boardList template");
        return "boardLists"; // 게시글 리스트 페이지로 이동
    }

    // 특정 게시글을 보여주는 메서드
    @GetMapping("/board/view")
    public String boardView(Model model, Integer id, @AuthenticationPrincipal UserDetails userDetails, Users user) {
        Board board = boardService.boardView(id); // 게시글 ID로 게시글을 가져옴
        List<Comment> comments = commentService.getCommentsByBoardId(id); // 게시글 ID로 댓글 리스트를 가져옴
        model.addAttribute("board", board); // 게시글 정보를 모델에 추가
        model.addAttribute("comments", comments); // 댓글 리스트를 모델에 추가
        model.addAttribute("loggedInUser", userDetails); // 로그인된 사용자 정보를 모델에 추가

        //  최근 본 상품 추가
        Long userId = getCurrentUserId();
        viewedPostService.addViewedPost(userId, Long.valueOf(id));

        String writer = board.getUser().getName();
        model.addAttribute("writer", writer);
        String writerPic = board.getUser().getProfilePictureUrl();
        model.addAttribute("writerPic", writerPic);

        Long userPostCount = boardService.getBoardCountByUserId(board.getUser().getId());
        model.addAttribute("userPostCount", userPostCount);




        return "BoardView"; // 게시글 상세 페이지로 이동
    }

    //  현재 사용자 ID를 가져오는 메서드 추가
    private Long getCurrentUserId() {
        // 실제 사용자 ID를 가져오는 로직으로 대체해야 합니다.
        return 1L; // 예시로 1을 반환
    }

    // 게시글 수정 폼을 보여주는 메서드
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id)); // 수정할 게시글 정보를 모델에 추가
        return "boardmodify"; // 게시글 수정 페이지로 이동
    }

    // 게시글 작성 처리 메서드
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        logger.info("게시글 작성 시작");

        board.setViewcount(0); // 조회수 초기화
        board.setCreateDate(LocalDateTime.now()); // 게시글 작성 시간 설정

        try {
            Board savedBoard = boardService.write(board, file); // 게시글 저장
            logger.info("게시글 저장 완료: " + savedBoard.getId());

            notificationService.notify(savedBoard); // 알림 생성
            logger.info("알림 생성 완료");

            model.addAttribute("message", "게시글 작성 완료했습니다.");
            model.addAttribute("redirectUrl", "/board/view?id=" + savedBoard.getId());
            return "message";
        } catch (Exception e) {
            logger.error("게시글 작성 중 오류 발생", e);
            model.addAttribute("message", "게시글 작성 중 오류가 발생했습니다."); // 오류 메시지를 모델에 추가
            model.addAttribute("redirectUrl", "/board/write"); // 게시글 작성 페이지 URL을 모델에 추가
            return "message"; // 메시지 페이지로 이동
        }
    }


    // 게시글 수정 처리 메서드
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id); // 기존 게시글 정보를 가져옴

        boardTemp.setTitle(board.getTitle()); // 게시글 제목 수정
        boardTemp.setContent(board.getContent()); // 게시글 내용 수정
        boardTemp.setModifyDate(LocalDateTime.now()); // 게시글 수정 시간 설정

        boardService.write(boardTemp, file); // 수정된 게시글 저장

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("redirectUrl", "/board/view?id=" + id);
        return "message";
    }

    // 댓글 추가 처리 메서드
    @PostMapping("/board/add-comment/{boardId}")
    public String addComment(@PathVariable Integer boardId, @AuthenticationPrincipal UserDetails userDetails, @RequestParam String content) {
        commentService.addComment(boardId, content, userDetails); // 댓글 추가
        return "redirect:/board/view?id=" + boardId; // 게시글 상세 페이지로 리다이렉트
    }




    // 파일 업로드 크기 초과 예외 처리 메서드
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        logger.warn("파일 업로드 크기 초과", e);
        redirectAttributes.addFlashAttribute("message", "파일 업로드 크기를 초과했습니다. 최대 파일 크기는 10MB입니다."); // 오류 메시지를 리다이렉트 속성에 추가
        return "redirect:/board/write"; // 게시글 작성 페이지로 리다이렉트
    }

    // 게시글 삭제 처리 메서드
    @GetMapping("/board/delete/{id}")
    public String boardDelete(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Board board = boardService.boardView(id);
        if (!board.getUser().getEmail().equals(userDetails.getUsername())) {
            model.addAttribute("message", "작성자만 글을 삭제할 수 있습니다.");
            model.addAttribute("redirectUrl", "/board/list");
            return "message";
        }

        notificationService.deleteNotificationsByBoardId(id); // 게시글의 알림 삭제
        commentService.deleteCommentsByBoardId(id); // 게시글의 댓글 삭제
        boardService.boardDelete(id); // 게시글 삭제

        model.addAttribute("message", "게시글 삭제 완료했습니다.");
        model.addAttribute("redirectUrl", "/board/list");
        return "message";
    }

        //  최근 본 상품 목록 조회 메서드 추가
    @GetMapping("/board/recentViewedPosts")
    public String recentViewedPosts(Model model) {
        Long userId = getCurrentUserId();
        List<ViewedPost> viewedPosts = viewedPostService.getRecentViewedPosts(userId);
        model.addAttribute("viewedPosts", viewedPosts);
        return "recentViewedPosts";
    }
}
