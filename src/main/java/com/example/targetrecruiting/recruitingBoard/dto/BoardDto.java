package com.example.targetrecruiting.recruitingBoard.dto;

import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.recruitingBoard.entity.BoardImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDto {
    private Long id;
//    private Long userId;
    private String title;
    private String content;
    private List<BoardImage> boardImageList;

    public BoardDto(Board board) {
        this.id = board.getId();
//        this.userId = board.getUser().getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardImageList = board.getBoardImageList();
    }
}
