package com.example.targetrecruiting.recruitingBoard.repository;

import com.example.targetrecruiting.recruitingBoard.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<BoardImage,Long> {
    void deleteByBoardId(Long boardId);
}
