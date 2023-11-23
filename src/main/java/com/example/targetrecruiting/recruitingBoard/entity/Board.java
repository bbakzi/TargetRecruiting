package com.example.targetrecruiting.recruitingBoard.entity;

import com.example.targetrecruiting.common.util.TimeStamped;
import com.example.targetrecruiting.recruitingBoard.dto.BoardRequestDto;
import com.example.targetrecruiting.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_Id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardImage>  boardImageList;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Builder
    public Board (BoardRequestDto boardRequestDto, User user){
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.user = user;
    }

    public void setImageFile(List<BoardImage> boardImageList){
        this.boardImageList = boardImageList;
    }
}
