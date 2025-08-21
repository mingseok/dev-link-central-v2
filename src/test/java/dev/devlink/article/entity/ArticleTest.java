package dev.devlink.article.entity;

import dev.devlink.article.exception.ArticleException;
import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArticleTest {

    private Member member;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.create(
                "김민석",
                "hong@example.com",
                "김민석닉네임",
                "encodedPassword"
        );
        setId(member, 1L);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    @DisplayName("게시글을 생성할 수 있다")
    void create_Success() {
        // when
        Article article = Article.create(member, "제목", "내용");

        // then
        assertThat(article.getTitle()).isEqualTo("제목");
        assertThat(article.getContent()).isEqualTo("내용");
        assertThat(article.getMember()).isEqualTo(member);
        assertThat(article.getViewCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("빌더 패턴으로 게시글을 생성할 수 있다")
    void builder_Success() {
        // when
        Article article = Article.builder()
                .member(member)
                .title("빌더 제목")
                .content("빌더 내용")
                .build();

        // then
        assertThat(article.getTitle()).isEqualTo("빌더 제목");
        assertThat(article.getContent()).isEqualTo("빌더 내용");
        assertThat(article.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("게시글 작성자 ID를 조회할 수 있다")
    void getWriterId_Success() throws Exception {
        // given
        Article article = Article.create(member, "제목", "내용");
        setId(article, 2L);

        // when
        Long writerId = article.getWriterId();

        // then
        assertThat(writerId).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 작성자 닉네임을 조회할 수 있다")
    void getWriterNickname_Success() {
        // given
        Article article = Article.create(member, "제목", "내용");

        // when
        String writerNickname = article.getWriterNickname();

        // then
        assertThat(writerNickname).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("게시글을 수정할 수 있다")
    void update_Success() {
        // given
        Article article = Article.create(member, "원본 제목", "원본 내용");

        // when
        article.update("수정된 제목", "수정된 내용");

        // then
        assertThat(article.getTitle()).isEqualTo("수정된 제목");
        assertThat(article.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("작성자인지 확인할 수 있다")
    void isAuthor_Success() throws Exception {
        // given
        Article article = Article.create(member, "제목", "내용");
        setId(article, 2L);

        // when & then
        assertThat(article.isAuthor(1L)).isTrue();
        assertThat(article.isAuthor(999L)).isFalse();
        assertThat(article.isAuthor(null)).isFalse();
    }

    @Test
    @DisplayName("작성자가 아니면 권한 체크에서 예외가 발생한다")
    void checkAuthor_NotAuthor_ThrowsException() throws Exception {
        // given
        Article article = Article.create(member, "제목", "내용");
        setId(article, 2L);
        Long otherMemberId = 999L;

        // when & then
        assertThatThrownBy(() -> article.checkAuthor(otherMemberId))
                .isInstanceOf(ArticleException.class);
    }

    @Test
    @DisplayName("작성자라면 권한 체크를 통과한다")
    void checkAuthor_IsAuthor_Success() throws Exception {
        // given
        Article article = Article.create(member, "제목", "내용");
        setId(article, 2L);

        // when & then
        article.checkAuthor(1L);
    }
}
