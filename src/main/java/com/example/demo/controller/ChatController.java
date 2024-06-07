package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatRoom;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    public ChatController(ChatMessageService chatMessageService, UserRepository userRepository, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
        this.chatRoomService = chatRoomService;
    }

    // 새로운 채팅을 시작하는 엔드포인트
    @GetMapping("/start")
    public ModelAndView startChat(@RequestParam Long senderId, @RequestParam Long receiverId) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(senderId, receiverId);
        Long chatRoomId = chatRoom.getId();
        return new ModelAndView("redirect:/chat/room/" + chatRoomId + "?receiverId=" + receiverId);
    }

    // 특정 채팅방을 불러오는 엔드포인트
    @GetMapping("/room/{chatRoomId}")
    public ModelAndView getChatRoom(@PathVariable Long chatRoomId, @RequestParam Long receiverId) {
        ModelAndView modelAndView = new ModelAndView("chatRoom");
        modelAndView.addObject("chatRoomId", chatRoomId);
        modelAndView.addObject("receiverId", receiverId);
        return modelAndView;
    }

    // 메시지를 보내는 엔드포인트
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long chatRoomId, @RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String content) {
        Users sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        Users receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId.toString());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        chatMessageService.saveChatMessage(message);

        return message;
    }

    // 특정 채팅방의 메시지 리스트를 가져오는 엔드포인트
    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long chatRoomId) {
        List<ChatMessage> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId.toString());
        return ResponseEntity.ok(messages);
    }

    // 사용자의 채팅방 목록과 마지막 메시지를 가져오는 엔드포인트
    @GetMapping("/chats")
    public List<Map<String, String>> getUserChatRooms(@RequestParam Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        List<ChatRoom> chatRooms = chatRoomService.findByUserId(userId);

        return chatRooms.stream().map(room -> {
            Map<String, String> chatRoomInfo = new HashMap<>();
            chatRoomInfo.put("chatRoomId", room.getId().toString());
            chatRoomInfo.put("partnerName", getPartnerName(room, userId));
            return chatRoomInfo;
        }).collect(Collectors.toList());
    }

    private String getPartnerName(ChatRoom room, Long userId) {
        Long partnerId = room.getSenderId().equals(userId) ? room.getReceiverId() : room.getSenderId();
        return userRepository.findById(partnerId).map(Users::getNickname).orElse("Unknown");
    }

    // 사용자의 채팅 페이지를 불러오는 엔드포인트
    @GetMapping("/chatter")
    public String getChatterPage(Model model, @RequestParam Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        List<Map<String, String>> chatRooms = getUserChatRooms(userId);
        model.addAttribute("user", user);
        model.addAttribute("chatRooms", chatRooms);
        return "chatter";
    }

//    // 사용자의 마이페이지를 불러오는 엔드포인트
//    @GetMapping("/mypage")
//    public String myPage(Model model, @RequestParam Long userId) {
//        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID")); // 사용자 확인
//        List<Map<String, String>> chatRooms = getUserChatRooms(userId); // 사용자의 채팅방 목록 가져옴
//        model.addAttribute("user", user); // 사용자 정보를 모델에 추가
//        model.addAttribute("chatRooms", chatRooms); // 채팅방 정보를 모델에 추가
//        return "mypage"; // 마이페이지로 이동
//    }
}
