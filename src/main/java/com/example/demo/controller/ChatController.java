package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(ChatMessageService chatMessageService, UserRepository userRepository) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/start")
    public ModelAndView startChat(@RequestParam Long senderId, @RequestParam Long receiverId) {
        String chatRoomId = UUID.randomUUID().toString();
        return new ModelAndView("redirect:/chat/room/" + chatRoomId + "?receiverId=" + receiverId);
    }

    @GetMapping("/room/{chatRoomId}")
    public ModelAndView getChatRoom(@PathVariable String chatRoomId, @RequestParam Long receiverId) {
        ModelAndView modelAndView = new ModelAndView("chatRoom");
        modelAndView.addObject("chatRoomId", chatRoomId);
        modelAndView.addObject("receiverId", receiverId);
        return modelAndView;
    }

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam String chatRoomId, @RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String content) {
        Users sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        Users receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return chatMessageService.saveChatMessage(message);
    }

    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable String chatRoomId) {
        List<ChatMessage> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/chats")
    public List<String> getUserChatRooms(@RequestParam Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        String userEmail = user.getEmail();
        List<ChatMessage> sentMessages = chatMessageService.getMessagesBySenderOrReceiver(user, user);
        List<ChatMessage> receivedMessages = chatMessageService.getMessagesBySenderOrReceiver(user, user);

        List<String> chatRooms = sentMessages.stream().map(ChatMessage::getChatRoomId).distinct().collect(Collectors.toList());
        chatRooms.addAll(receivedMessages.stream().map(ChatMessage::getChatRoomId).distinct().collect(Collectors.toList()));
        return chatRooms.stream().distinct().collect(Collectors.toList());
    }

    @GetMapping("/mypage")
    public String myPage(Model model, @RequestParam Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        List<String> chatRooms = getUserChatRooms(userId);
        model.addAttribute("user", user);
        model.addAttribute("chatRooms", chatRooms);
        return "mypage";
    }

    @GetMapping("/chatRoom")
    public String chatRoom(@RequestParam("postAuthor") String postAuthor, @RequestParam("chatRoomId") String chatRoomId, Model model) {
        model.addAttribute("postAuthor", postAuthor);
        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("receiverId", postAuthor); // 게시글 작성자를 수신자로 설정
        return "chatRoom";
    }

    @GetMapping("/chatter")
    public String chatter(@RequestParam("postAuthor") String postAuthor, @RequestParam("chatRoomId") String chatRoomId, Model model) {
        model.addAttribute("postAuthor", postAuthor);
        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("receiverId", postAuthor); // 게시글 작성자를 수신자로 설정
        return "chatter";
    }
}
