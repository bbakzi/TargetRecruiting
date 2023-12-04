package com.example.targetrecruiting.recruitingBoard.repository;

import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.querydsl.QPageRequest;

public interface BoardInQuery {
    Slice<BoardDto> getBoards(Long lastBoardId, PageRequest pageRequest, String category, String filter);

    void increaseViews(Long boardId);
}
