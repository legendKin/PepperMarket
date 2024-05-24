package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
    // 게시글 작성 폼을 표시
    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String username = principalDetails.getUsername(); // 현재 사용자의 별명 또는 닉네임 가져오기
        model.addAttribute("username", username); // 모델에 사용자 별명 전달
        return "boardwrite";  // 게시글 작성 폼 뷰 이름 반환
    }

    // 게시글 목록을 페이징 및 검색 기능과 함께 표시
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);  // 게시글 목록 조회
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);  // 검색 키워드로 게시글 목록 조회
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        int totalPage = list.getTotalPages();

        model.addAttribute("list", list);  // 게시글 목록을 모델에 추가
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", totalPage);

        return "boardList";  // 게시글 목록 뷰 이름 반환
    }

    // 특정 게시글 및 댓글 표시
    @GetMapping("/board/view")
    public String boardview(Model model, Integer id) {
        boardService.boardViewCount(id);  // 게시글 조회수 증가
        model.addAttribute("board", boardService.boardView(id));  // 게시글을 모델에 추가
        List<Comment> comments = commentService.getCommentsByBoardId(id);  // 게시글의 댓글 조회
        model.addAttribute("comments", comments);  // 댓글을 모델에 추가
        return "boardView";  // 게시글 보기 뷰 이름 반환
    }

    // 특정 게시글 삭제
    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        commentService.deleteCommentsByBoardId(id);
        boardService.boardDelete(id);  // 게시글 삭제

        return "redirect:/board/list";  // 삭제 후 게시글 목록으로 리디렉션
    }

    // 특정 게시글 수정 폼 표시
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));  // 게시글을 모델에 추가
        return "boardmodify";  // 게시글 수정 폼 뷰 이름 반환
    }

    // 새로운 게시글 작성 폼 제출 처리
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        board.setViewcount(0);  // 초기 조회수 0으로 설정
        board.setCreateDate(LocalDateTime.now());  // 생성 날짜를 현재 시간으로 설정
        boardService.write(board, file);  // 새로운 게시글 저장

        model.addAttribute("message", "글 작성이 완료 되었습니다.");  // 성공 메시지를 모델에 추가
        model.addAttribute("searchUrl", "/board/list");  // 작성 후 리디렉션할 URL 설정

        return "message";  // 성공 메시지 뷰 이름 반환
    }

    // 기존 게시글 수정 폼 제출 처리
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id);  // 기존 게시글 조회

        boardTemp.setTitle(board.getTitle());  // 제목 업데이트
        boardTemp.setContent(board.getContent());  // 내용 업데이트
        boardTemp.setModifyDate(LocalDateTime.now());  // 수정 날짜를 현재 시간으로 설정

        boardService.write(boardTemp, file);  // 수정된 게시글 저장

        model.addAttribute("message", "글 수정이 완료 되었습니다.");  // 성공 메시지를 모델에 추가
        model.addAttribute("searchUrl", "/board/list");  // 수정 후 리디렉션할 URL 설정

        return "message";  // 성공 메시지 뷰 이름 반환
    }

    // 특정 게시글에 댓글 추가
    @PostMapping("/board/add-comment/{boardId}")
    public String addComment(@PathVariable Integer boardId, @RequestParam String content, @RequestParam String author) {
        commentService.addComment(boardId, content, author);  // 댓글 추가
        return "redirect:/board/view?id=" + boardId;  // 댓글 추가 후 게시글 보기로 리디렉션
    }

    // 파일 다운로드 요청 처리
    @GetMapping("/board/download/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        String fileStoragePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        Path filePath = Paths.get(fileStoragePath).resolve(fileName);

        try {
            byte[] fileContent = Files.readAllBytes(filePath);  // 파일 내용 읽기

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileContent.length);
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(fileContent, headers, org.springframework.http.HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, org.springframework.http.HttpStatus.NOT_FOUND);
        }
    }






    // 테스트페이지입니다.
    @GetMapping("/bal")
    public String bal(Model model,
                            @PageableDefault(page = 0, size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);  // 게시글 목록 조회
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);  // 검색 키워드로 게시글 목록 조회
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        int totalPage = list.getTotalPages();

        model.addAttribute("list", list);  // 게시글 목록을 모델에 추가
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", totalPage);

        return "BoardAllLists";  // 게시글 목록 뷰 이름 반환
    }
}
