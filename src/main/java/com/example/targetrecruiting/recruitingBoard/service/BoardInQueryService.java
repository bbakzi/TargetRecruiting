package com.example.targetrecruiting.recruitingBoard.service;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.recruitingBoard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardInQueryService {
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public ResponseDto<Slice<BoardDto>> getAllBoards(Long id, int size, String category, String filter){
        PageRequest pageRequest = PageRequest.of(0,size);

        Slice<BoardDto> boardDtoSlice =
                boardRepository.getBoards(id, pageRequest,category, filter);

        List<BoardDto> content = boardDtoSlice.getContent();
        Long lastBoardId = null;
        if (!content.isEmpty()){
            BoardDto lastBoardDto = content.get(content.size()-1);
            lastBoardId = lastBoardDto.getId();
        }
        return ResponseDto.setSuccess(HttpStatus.OK, String.valueOf(lastBoardId), boardDtoSlice);
    }

    @Transactional
    public ResponseDto<BoardDto> getBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boardRepository.increaseViews(id);
        BoardDto boardDto = new BoardDto(board);
        return ResponseDto.setSuccess(HttpStatus.OK,"조회 완료",boardDto);
    }
}
