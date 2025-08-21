package dev.devlink.comment.entity;

import dev.devlink.article.entity.Article;
import dev.devlink.comment.exception.CommentException;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("게시글 댓글 엔티티 테스트")
class ArticleCommentTest {

    private Member author;
    private Member commenter;
    private Article article;

    @BeforeEach
    void setUp() {
        author = Member.builder()
                .name("작성자")
                .email("author@example.com")
                .nickname("작성자닉네임")
                .password("password")
                .build();

        commenter = Member.builder()
                .name("댓글작성자")
                .email("commenter@example.com")
                .nickname("댓글작성자닉네임")
                .password("password")
                .build();

        article = Article.create(author, "게시글 제목", "게시글 내용");
    }

    @Test
    @DisplayName("게시글 댓글을 생성할 수 있다")
    void create_Success() {
        // when
        ArticleComment comment = ArticleComment.create(
                article,
                commenter,
                null,
                "댓글 내용입니다."
        );

        // then
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getMember()).isEqualTo(commenter);
        assertThat(comment.getContent()).isEqualTo("댓글 내용입니다.");
        assertThat(comment.getParentId()).isNull();
    }

    @Test
    @DisplayName("대댓글을 생성할 수 있다")
    void create_WithParentId_Success() {
        // given
        Long parentCommentId = 1L;

        // when
        ArticleComment reply = ArticleComment.create(
                article,
                commenter,
                parentCommentId,
                "대댓글 내용입니다."
        );

        // then
        assertThat(reply.getArticle()).isEqualTo(article);
        assertThat(reply.getMember()).isEqualTo(commenter);
        assertThat(reply.getContent()).isEqualTo("대댓글 내용입니다.");
        assertThat(reply.getParentId()).isEqualTo(parentCommentId);
    }

    @Test
    @DisplayName("댓글 작성자 닉네임을 반환한다")
    void getWriterNickname_ReturnsCorrectNickname() {
        // given
        ArticleComment comment = ArticleComment.create(article, commenter, null, "댓글 내용");

        // when
        String writerNickname = comment.getWriterNickname();

        // then
        assertThat(writerNickname).isEqualTo("댓글작성자닉네임");
    }

    @Test
    @DisplayName("댓글 작성자를 반환한다")
    void getWriter_ReturnsCorrectWriter() {
        // given
        ArticleComment comment = ArticleComment.create(article, commenter, null, "댓글 내용");

        // when
        Member writer = comment.getWriter();

        // then
        assertThat(writer).isEqualTo(commenter);
    }

    @Test
    @DisplayName("작성자가 맞으면 권한 확인을 통과한다")
    void checkAuthor_WithAuthorId_Success() {
        // given
        ArticleComment comment = ArticleComment.create(article, commenter, null, "댓글 내용");

        // when & then
        comment.checkAuthor(commenter.getId());
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("작성자가 아니면 권한 확인에서 예외가 발생한다")
    void checkAuthor_WithOtherId_ThrowsException() {
        // given
        ArticleComment comment = ArticleComment.create(article, commenter, null, "댓글 내용");

        // when & then
        assertThatThrownBy(() -> comment.checkAuthor(author.getId()))
                .isInstanceOf(CommentException.class);
    }

    @Test
    @DisplayName("빌더 패턴으로 댓글을 생성할 수 있다")
    void builder_Success() {
        // when
        ArticleComment comment = ArticleComment.builder()
                .article(article)
                .member(commenter)
                .content("빌더로 생성한 댓글")
                .parentId(5L)
                .build();

        // then
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getMember()).isEqualTo(commenter);
        assertThat(comment.getContent()).isEqualTo("빌더로 생성한 댓글");
        assertThat(comment.getParentId()).isEqualTo(5L);
    }
}
