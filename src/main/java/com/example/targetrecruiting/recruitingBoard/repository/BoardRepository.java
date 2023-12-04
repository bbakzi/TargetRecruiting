package com.example.targetrecruiting.recruitingBoard.repository;

import com.example.targetrecruiting.recruitingBoard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardInQuery {

}
