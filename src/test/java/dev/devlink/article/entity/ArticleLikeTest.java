package dev.devlink.article.entity;

import dev.devlink.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleLikeTest {

    private Member member;
    private Article article;

    @BeforeEach
    void setUp() {
        member = Member.create(
                "김민석",
                "hong@example.com",
                "김민석닉네임",
                "encodedPassword"
        );

        article = Article.create(member, "테스트 제목", "테스트 내용");
    }

    @Test
    @DisplayName("게시글 좋아요를 생성할 수 있다")
    void create_Success() {
        // when
        ArticleLike articleLike = ArticleLike.create(article, member);

        // then
        assertThat(articleLike.getArticle()).isEqualTo(article);
        assertThat(articleLike.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("빌더 패턴으로 게시글 좋아요를 생성할 수 있다")
    void builder_Success() {
        // when
        ArticleLike articleLike = ArticleLike.builder()
                .article(article)
                .member(member)
                .build();

        // then
        assertThat(articleLike.getArticle()).isEqualTo(article);
        assertThat(articleLike.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("다른 회원이 같은 게시글에 좋아요를 생성할 수 있다")
    void create_DifferentMember_Success() {
        // given
        Member anotherMember = Member.create(
                "김선우",
                "kim@example.com",
                "김선우닉네임",
                "anotherPassword"
        );

        // when
        ArticleLike articleLike1 = ArticleLike.create(article, member);
        ArticleLike articleLike2 = ArticleLike.create(article, anotherMember);

        // then
        assertThat(articleLike1.getArticle()).isEqualTo(article);
        assertThat(articleLike1.getMember()).isEqualTo(member);
        assertThat(articleLike2.getArticle()).isEqualTo(article);
        assertThat(articleLike2.getMember()).isEqualTo(anotherMember);
    }

    @Test
    @DisplayName("같은 회원이 다른 게시글에 좋아요를 생성할 수 있다")
    void create_DifferentArticle_Success() {
        // given
        Article anotherArticle = Article.create(member, "다른 제목", "다른 내용");

        // when
        ArticleLike articleLike1 = ArticleLike.create(article, member);
        ArticleLike articleLike2 = ArticleLike.create(anotherArticle, member);

        // then
        assertThat(articleLike1.getArticle()).isEqualTo(article);
        assertThat(articleLike1.getMember()).isEqualTo(member);
        assertThat(articleLike2.getArticle()).isEqualTo(anotherArticle);
        assertThat(articleLike2.getMember()).isEqualTo(member);
    }
}
