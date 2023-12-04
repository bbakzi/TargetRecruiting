package com.example.targetrecruiting.recruitingBoard.controller;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.common.security.UserDetailsImpl;
import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import com.example.targetrecruiting.recruitingBoard.dto.BoardRequestDto;
import com.example.targetrecruiting.recruitingBoard.service.BoardInQueryService;
import com.example.targetrecruiting.recruitingBoard.service.BoardService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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
    private final BoardInQueryService boardInQueryService;

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
        return boardInQueryService.getBoard(id);
    }
    //게시글 전체 조회
    @GetMapping
    public ResponseDto<Slice<BoardDto>> getAllBoards(@RequestParam (name = "last-board-id")Long id,
                                           @RequestParam(name = "size") int size,
                                           @Nullable @RequestParam (name = "category") String category,
                                           @Nullable @RequestParam (name = "filter") String filter){
        return boardInQueryService.getAllBoards(id ,size, category, filter);
    }

}
