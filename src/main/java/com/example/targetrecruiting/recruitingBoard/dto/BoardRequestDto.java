package com.example.targetrecruiting.recruitingBoard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private  String content;
    private String category;
    private String filter;
    private Long views;
}
