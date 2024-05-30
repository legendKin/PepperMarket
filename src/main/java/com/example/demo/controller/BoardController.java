package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.KeywordService;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
//@RequestMapping("/board")
public class BoardController {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/board/write")
    public String boardWriteForm(Model model, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String username = principalDetails.getUsername();
        model.addAttribute("username", username);
        return "BoardWrite";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPage", list.getTotalPages());

        return "BoardLists";
    }

    @GetMapping("/board/view")
    public String boardview(Model model, Integer id) {
        boardService.boardViewCount(id);
        model.addAttribute("board", boardService.boardView(id));
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        model.addAttribute("comments", comments);
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

    @GetMapping("/board/download/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        String fileStoragePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        Path filePath = Paths.get(fileStoragePath).resolve(fileName);

        try {
            byte[] fileContent = Files.readAllBytes(filePath);

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

    @PostMapping("/add")
    public String addBoard(@RequestBody Board board) {
        System.out.println("Adding board: " + board.getTitle());
        boardService.saveBoard(board);
        return "Board added";
    }

    @GetMapping("/list/category/{categoryId}")
    public String boardListByCategory(@PathVariable Long categoryId, Model model) {
        List<Board> boards = boardService.boardListByCategory(categoryId);
        model.addAttribute("list", boards);
        return "boardListByCategory";
    }
}
