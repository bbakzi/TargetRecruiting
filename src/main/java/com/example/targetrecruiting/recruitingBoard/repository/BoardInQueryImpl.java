package com.example.targetrecruiting.recruitingBoard.repository;


import com.example.targetrecruiting.recruitingBoard.dto.BoardDto;
import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.recruitingBoard.entity.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.example.targetrecruiting.recruitingBoard.entity.QBoard.board;
import static com.example.targetrecruiting.user.entity.QUser.user;


public class BoardInQueryImpl extends QuerydslRepositorySupport implements BoardInQuery {

    @PersistenceContext
    private EntityManager entityManager;

    public BoardInQueryImpl() {
        super(Board.class);
    }

    @Override
    public Slice<BoardDto> getBoards(@Nullable Long lastBoardId, PageRequest pageRequest, @Nullable String category,
                                     @Nullable String filter) {
        QBoard board = QBoard.board;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(ltBoardId(lastBoardId));

        if (category != null && !category.isEmpty()) {
            whereBuilder.and(findByCategory(category));
        }
        if (filter != null && !filter.isEmpty()) {
            whereBuilder.and(findByFilter(filter));
        }

        List<Board> resultSlice = queryFactory
                .select(board)
                .from(board)
                .join(board.user, user)
                .fetchJoin()
                .where(whereBuilder)
                .orderBy(board.id.desc())
                .limit((long) pageRequest.getPageSize() + 1)
                .fetch();

        List<BoardDto> content = resultSlice.stream()
                .map(p -> new BoardDto(p, p.getUser()))
                .toList();

        boolean hashNext = content.size() >= pageRequest.getPageSize();

        return new SliceImpl<>(content, pageRequest, hashNext);
    }

    @Override
    public void increaseViews(Long boardId) {
        QBoard board = QBoard.board;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.update(board)
                .set(board.views, board.views.add(1))
                .where(board.id.eq(boardId))
                .execute();
    }

    private BooleanExpression ltBoardId(Long id) {
        return id == null ? null : board.id.lt(id);
    }

    private BooleanExpression findByCategory(String category) {
        return category == null || category.isEmpty() ? null : board.category.eq(category);
    }

    private BooleanExpression findByFilter(String filter) {
        return filter == null || filter.isEmpty() ? null : board.filter.eq(filter);
    }
}
