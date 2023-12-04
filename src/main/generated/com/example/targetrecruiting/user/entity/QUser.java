package com.example.targetrecruiting.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -860591525L;

    public static final QUser user = new QUser("user");

    public final com.example.targetrecruiting.common.util.QTimeStamped _super = new com.example.targetrecruiting.common.util.QTimeStamped(this);

    public final ListPath<com.example.targetrecruiting.recruitingBoard.entity.Board, com.example.targetrecruiting.recruitingBoard.entity.QBoard> boardList = this.<com.example.targetrecruiting.recruitingBoard.entity.Board, com.example.targetrecruiting.recruitingBoard.entity.QBoard>createList("boardList", com.example.targetrecruiting.recruitingBoard.entity.Board.class, com.example.targetrecruiting.recruitingBoard.entity.QBoard.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath profileImage = createString("profileImage");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

