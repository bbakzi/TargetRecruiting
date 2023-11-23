package com.example.targetrecruiting.recruitingBoard.controller;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.common.security.UserDetailsImpl;
import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import com.example.targetrecruiting.recruitingBoard.dto.BoardRequestDto;
import com.example.targetrecruiting.recruitingBoard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    //게시글 작성
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<BoardDto> createBoard(@RequestPart BoardRequestDto boardRequestDto,
                                             @RequestPart(name = "boardImage", required = false)List<MultipartFile> images,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException{
        return boardService.createBoard(boardRequestDto,images,userDetails.user());
    }
    //게시글 한개 조회
    @GetMapping("/{board-id}")
    public ResponseDto<BoardDto> getBoard(@PathVariable (name = "board-id") Long id){
        return boardService.getBoard(id);
    }
    //게시글 모두 조회
    //게시글 수정
    //게시글 삭제

}
