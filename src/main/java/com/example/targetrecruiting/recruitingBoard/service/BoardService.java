package com.example.targetrecruiting.recruitingBoard.service;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.common.util.S3Service;
import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import com.example.targetrecruiting.recruitingBoard.dto.BoardRequestDto;
import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.recruitingBoard.repository.BoardRepository;
import com.example.targetrecruiting.recruitingBoard.repository.FileRepository;
import com.example.targetrecruiting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;

    //게시글 작성
    @Transactional
    public ResponseDto<BoardDto> createBoard(BoardRequestDto boardRequestDto,
                                             List<MultipartFile> images, User user) throws IOException {

        Board board = new Board(boardRequestDto, user);
        board.setImageFile(s3Service.fileFactory(images, board));
        Board savedBoard = boardRepository.save(board);

        return ResponseDto.setSuccess(HttpStatus.OK,"게시글 작성 완료",new BoardDto(savedBoard));
    }

    //게시글 상세조회
    @Transactional(readOnly = true)
    public ResponseDto<BoardDto> getBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return ResponseDto.setSuccess(HttpStatus.OK,"게시글 상세조회 성공", new BoardDto(board));
    }
}
