package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatRoom;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.entity.PrincipalDetails;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final BoardService boardService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService, UserRepository userRepository, ChatRoomService chatRoomService, BoardService boardService) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
        this.chatRoomService = chatRoomService;
        this.boardService = boardService;
    }
  

    // 새로운 채팅을 시작하는 엔드포인트
    @GetMapping("/start")
    public ModelAndView startChat(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam Long postId) {
        ChatRoom chatRoom = chatRoomService.createOrGetChatRoom(senderId, receiverId, postId);
        Long chatRoomId = chatRoom.getId();
        return new ModelAndView("redirect:/chat/room/" + chatRoomId + "?receiverId=" + receiverId);
    }

    // 특정 채팅방을 불러오는 엔드포인트
    @GetMapping("/room/{chatRoomId}")
    public ModelAndView getChatRoom(@PathVariable Long chatRoomId, @RequestParam Long receiverId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ModelAndView modelAndView = new ModelAndView("chatRoom");
        modelAndView.addObject("chatRoomId", chatRoomId); // 채팅방 ID를 모델에 추가
        modelAndView.addObject("receiverId", receiverId); // 수신자 ID를 모델에 추가
        modelAndView.addObject("userId", principalDetails.getId()); // 수신자 ID를 모델에 추가
        return modelAndView;
    }

    // 메시지를 보내는 엔드포인트
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long chatRoomId, @RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String content) {
        Users sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID")); // 발신자 확인
        Users receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID")); // 수신자 확인

        // 새로운 채팅 메시지 객체 생성 및 설정
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        chatMessageService.saveChatMessage(message); // 메시지 저장 후 반환
        return message;
    }

    // 특정 채팅방의 메시지 리스트를 가져오는 엔드포인트
    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long chatRoomId) {
        List<ChatMessage> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(messages); // 메시지 리스트를 HTTP 응답으로 반환
    }

    // 사용자의 채팅방 목록과 마지막 메시지를 가져오는 엔드포인트
    @GetMapping("/chats")
    public List<Map<String, String>> getUserChatRooms(@RequestParam Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID")); // 사용자 확인
        List<ChatRoom> chatRooms = chatRoomService.findByUserId(userId); // 사용자의 채팅방 목록 가져옴

        // 채팅방 정보와 마지막 메시지를 맵 형태로 변환하여 리스트로 반환
        return chatRooms.stream().map(room -> {
            Map<String, String> chatRoomInfo = new HashMap<>();
            chatRoomInfo.put("chatRoomId", room.getId().toString());
            chatRoomInfo.put("partnerName", getPartnerName(room, userId)); // 상대방 이름 추가
            chatRoomInfo.put("partnerId", String.valueOf(room.getReceiverId().equals(userId) ? room.getSenderId() : room.getReceiverId()));
            chatRoomInfo.put("postId", String.valueOf(room.getPostId()));

            // 게시글의 제목 가져오기
            String postTitle = boardService.getBoardTitleByPostId(room.getPostId()); // postId로 게시글 제목 가져오기
            chatRoomInfo.put("postTitle", postTitle);
            return chatRoomInfo;
        }).collect(Collectors.toList());
    }

    // 게시글 제목 받아오는 엔드포인트
    @GetMapping("/postTitle/{postId}")
    public ResponseEntity<String> getPostTitle(@PathVariable Long postId) {
        String postTitle = boardService.getBoardTitleByPostId(postId);
        return ResponseEntity.ok(postTitle);
    }

    // 특정 채팅방의 정보를 가져오는 엔드포인트
    @GetMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<Map<String, Object>> getChatRoomInfo(@PathVariable Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomService.findByChatRoomId(chatRoomId);
        if (chatRoom.isPresent()) {
            ChatRoom room = chatRoom.get();
            Map<String, Object> chatRoomInfo = new HashMap<>();
            chatRoomInfo.put("partnerName", getPartnerName(room, room.getSenderId())); // 상대방 이름 추가
            chatRoomInfo.put("postId", room.getPostId());
            return ResponseEntity.ok(chatRoomInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private String getPartnerName(ChatRoom room, Long userId) {
        Long partnerId = room.getSenderId().equals(userId) ? room.getReceiverId() : room.getSenderId();
        return userRepository.findById(partnerId).map(Users::getNickname).orElse("Unknown");
    }
}
