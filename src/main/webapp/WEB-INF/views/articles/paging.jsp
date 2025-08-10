<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/articles/paging.css">

    <title>Dev Link Central - 스터디 모집</title>
</head>
<body>
    <div class="articles-container">
        <!-- 헤더 섹션 -->
        <div class="header-section">
            <div class="header-left">
                <div class="brand-info">
                    <i class="fas fa-newspaper brand-icon"></i>
                    <div class="header-text">
                        <h1 class="page-title">스터디 모집 게시판</h1>
                        <p class="page-subtitle">함께 성장할 스터디 메이트를 찾아보세요</p>
                    </div>
                </div>
            </div>
            <div class="header-right">
                <button onclick="saveReq()" class="btn btn-write">
                    <i class="fas fa-plus"></i>
                    <span>글 작성</span>
                </button>
                <button onclick="home()" class="btn btn-back">
                    <i class="fas fa-home"></i>
                    <span>홈으로</span>
                </button>
            </div>
        </div>

        <!-- 인기글 섹션 -->
        <div class="popular-section">
            <div class="section-header">
                <i class="fas fa-fire"></i>
                <h3>인기 스터디 TOP 5</h3>
            </div>
            <div id="topArticlesList" class="popular-list">
                <!-- JS에서 동적으로 인기글 삽입 -->
                <div class="loading-item">
                    <i class="fas fa-spinner fa-spin"></i>
                    <span>인기 글을 불러오는 중...</span>
                </div>
            </div>
        </div>

        <!-- 게시글 목록 섹션 -->
        <div class="content-section">
            <div class="section-header">
                <i class="fas fa-list"></i>
                <h3>전체 스터디 목록</h3>
            </div>
            <div id="articlesList" class="articles-list">
                <!-- JS에서 동적으로 게시글 삽입 -->
                <div class="loading-state">
                    <div class="loading-spinner">
                        <i class="fas fa-spinner fa-spin"></i>
                    </div>
                    <h3>게시글을 불러오는 중...</h3>
                    <p>잠시만 기다려주세요</p>
                </div>
            </div>
            
            <!-- 페이지네이션 -->
            <div class="pagination-container">
                <nav class="pagination" id="paginationContainer">
                    <!-- JS에서 동적으로 페이지네이션 삽입 -->
                </nav>
            </div>
        </div>
    </div>

    <script>
        function home() {
            window.location.href = "/view/members/home";
        }

        function saveReq() {
            window.location.href = "/view/articles/save";
        }

        $(document).ready(function () {
            const urlParams = new URLSearchParams(window.location.search);
            const pageParam = urlParams.get('page');
            const currentPage = pageParam && !isNaN(pageParam) ? parseInt(pageParam) : 0;

            const pageSize = 8;
            loadArticles(currentPage, pageSize);
            loadTopArticles();
        });

        function loadArticles(page, size) {
            const validPage = (typeof page === 'number' && !isNaN(page)) ? page : 0;
            const validSize = (typeof size === 'number' && !isNaN(size)) ? size : 8;

            const apiUrl = '/api/public/articles?page=' + validPage + '&size=' + validSize;

            $.ajax({
                url: apiUrl,
                type: 'GET',
                success: function (res) {
                    const articlePage = res.data;
                    renderArticles(articlePage.content);
                    renderPagination(articlePage);
                },
                error: function () {
                    $('#articlesList').html('' +
                        '<div class="error-state">' +
                            '<div class="error-icon">' +
                                '<i class="fas fa-exclamation-triangle"></i>' +
                            '</div>' +
                            '<h3>오류가 발생했습니다</h3>' +
                            '<p>게시글을 불러오는 중 문제가 발생했습니다.</p>' +
                            '<button class="btn btn-retry" onclick="window.location.reload()">' +
                                '<i class="fas fa-redo"></i>' +
                                '<span>다시 시도</span>' +
                            '</button>' +
                        '</div>'
                    );
                }
            });
        }

        function renderArticles(articles) {
            const container = $('#articlesList');
            container.empty();

            if (!articles || articles.length === 0) {
                container.html('' +
                    '<div class="empty-state">' +
                        '<div class="empty-icon">' +
                            '<i class="fas fa-file-alt"></i>' +
                        '</div>' +
                        '<h3>등록된 스터디가 없습니다</h3>' +
                        '<p>첫 번째 스터디를 등록해보세요!</p>' +
                        '<button class="btn btn-write" onclick="saveReq()">' +
                            '<i class="fas fa-plus"></i>' +
                            '<span>스터디 등록하기</span>' +
                        '</button>' +
                    '</div>'
                );
                return;
            }

            articles.forEach(article => {
                console.log("id:", article.id);
                console.log("title:", article.title);
                console.log("writer:", article.writer);
                console.log("writerId:", article.writerId);
                console.log("created:", article.formattedCreatedAt);

                const articleCard = '' +
                    '<div class="article-card" onclick="viewArticle(' + article.id + ')">' +
                        '<div class="article-header">' +
                            '<div class="article-meta">' +
                                '<span class="article-id">#' + article.id + '</span>' +
                                '<span class="article-author" onclick="event.stopPropagation(); viewProfile(' + article.writerId + ')">' +
                                    '<i class="fas fa-user"></i>' +
                                    article.writer +
                                '</span>' +
                                '<span class="article-date">' +
                                    '<i class="fas fa-calendar-alt"></i>' +
                                    article.formattedCreatedAt +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="article-content">' +
                            '<h4 class="article-title">' + article.title + '</h4>' +
                        '</div>' +
                        '<div class="article-footer">' +
                            '<div class="article-stats">' +
                                '<span class="stat-item">' +
                                    '<i class="fas fa-eye"></i>' +
                                    '<span>조회</span>' +
                                '</span>' +
                                '<span class="stat-item">' +
                                    '<i class="fas fa-comment"></i>' +
                                    '<span>댓글</span>' +
                                '</span>' +
                            '</div>' +
                            '<div class="article-action">' +
                                '<i class="fas fa-arrow-right"></i>' +
                            '</div>' +
                        '</div>' +
                    '</div>';

                container.append(articleCard);
            });
        }

        function viewArticle(articleId) {
            window.location.href = "/view/articles/" + articleId;
        }

        function viewProfile(writerId) {
            window.location.href = "/view/profile/" + writerId;
        }

        function loadTopArticles() {
            $.ajax({
                url: '/api/public/articles/best',
                type: 'GET',
                success: function (res) {
                    const topArticles = res.data;
                    renderTopArticles(topArticles);
                },
                error: function () {
                    $('#topArticlesList').html('' +
                        '<div class="error-item">' +
                            '<i class="fas fa-exclamation-triangle"></i>' +
                            '<span>인기 글을 불러올 수 없습니다</span>' +
                        '</div>'
                    );
                }
            });
        }

        function renderTopArticles(articles) {
            const container = $('#topArticlesList');
            container.empty();

            if (!articles || articles.length === 0) {
                container.html('' +
                    '<div class="empty-item">' +
                        '<i class="fas fa-info-circle"></i>' +
                        '<span>인기 스터디가 없습니다</span>' +
                    '</div>'
                );
                return;
            }

            articles.forEach((article, index) => {
                const rank = index + 1;
                const rankClass = rank <= 3 ? 'top-rank' : 'normal-rank';
                
                const item = '' +
                    '<div class="popular-item ' + rankClass + '" onclick="viewArticle(' + article.id + ')">' +
                        '<div class="rank-badge">' +
                            '<span class="rank-number">' + rank + '</span>' +
                        '</div>' +
                        '<div class="popular-content">' +
                            '<h5 class="popular-title">' + article.title + '</h5>' +
                            '<div class="popular-stats">' +
                                '<span class="view-count">' +
                                    '<i class="fas fa-eye"></i>' +
                                    article.viewCount + ' 조회' +
                                '</span>' +
                            '</div>' +
                        '</div>' +
                        '<div class="popular-action">' +
                            '<i class="fas fa-chevron-right"></i>' +
                        '</div>' +
                    '</div>';
                
                container.append(item);
            });
        }

        function renderPagination(pageInfo) {
            const pagination = $('#paginationContainer');
            pagination.empty();

            const current = pageInfo.number;
            const total = pageInfo.totalPages;

            if (total <= 1) return;

            const blockSize = 5;
            const start = Math.floor(current / blockSize) * blockSize;
            const end = Math.min(start + blockSize, total);

            // 처음 버튼
            if (current > 0) {
                pagination.append('<a href="?page=0" class="page-btn"><i class="fas fa-angle-double-left"></i></a>');
            }

            // 이전 버튼
            if (current > 0) {
                pagination.append('<a href="?page=' + (current - 1) + '" class="page-btn"><i class="fas fa-angle-left"></i></a>');
            }

            // 페이지 번호들
            for (let i = start; i < end; i++) {
                if (i === current) {
                    pagination.append('<span class="page-btn active">' + (i + 1) + '</span>');
                } else {
                    pagination.append('<a href="?page=' + i + '" class="page-btn">' + (i + 1) + '</a>');
                }
            }

            // 다음 버튼
            if (current + 1 < total) {
                pagination.append('<a href="?page=' + (current + 1) + '" class="page-btn"><i class="fas fa-angle-right"></i></a>');
            }

            // 마지막 버튼
            if (current + 1 < total) {
                pagination.append('<a href="?page=' + (total - 1) + '" class="page-btn"><i class="fas fa-angle-double-right"></i></a>');
            }
        }
    </script>
</body>
</html>