package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl extends BoardService {
	
	@Autowired
	private BoardRepository boardRepository;
	
	public BoardServiceImpl(UserRepository userRepository) {
		super(userRepository);
	}
	
	@Override
	public Page<Board> searchBoards(String searchKeyword, Integer searchCateID, Pageable pageable, boolean showCompleted) {
		Integer status = showCompleted ? null : 3; // showCompleted가 false일 때만 status가 3이 아닌 게시글을 필터링
		return boardRepository.searchBoards(searchKeyword, searchCateID, status, pageable);
	}
}