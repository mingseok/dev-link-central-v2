<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <!-- Bootstrap CSS -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Popper.js -->
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/umd/popper.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- SweetAlert2 CSS and JS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/articles/paging.css">
</head>
<body>
<div class="container">
    <div class="header">
        <div class="title">스터디 모집 게시판</div>
        <div class="header-actions">
            <button onclick="home()" style="margin-left: 10px;">나가기</button>
            <button id="writeButton" onclick="saveReq()" style="margin-left: 8px;">글작성</button>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>글번호</th>
            <th>작성자</th>
            <th>스터디 제목</th>
            <th>작성일</th>
        </tr>
        </thead>
        <tbody id="articleTableBody">
        <!-- JS가 여기에 동적으로 게시글을 렌더링함 -->
        </tbody>
    </table>

    <nav class="pagination" id="paginationContainer"></nav>
</div>

<!-- 스크립트: 기존 버튼/링크/토큰 처리 유지 -->
<script>
    function home() {
        window.location.href = "/api/v1/view/members/home";
    }

    function saveReq() {
        window.location.href = "/api/v1/view/articles/save";
    }

    $(document).ready(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const pageParam = urlParams.get('page');
        const currentPage = pageParam && !isNaN(pageParam) ? parseInt(pageParam) : 0;

        const pageSize = 8;
        loadArticles(currentPage, pageSize);
    });

    function loadArticles(page, size) {
        const validPage = (typeof page === 'number' && !isNaN(page)) ? page : 0;
        const validSize = (typeof size === 'number' && !isNaN(size)) ? size : 8;

        const apiUrl = '/api/v1/public/articles?page=' + validPage + '&size=' + validSize;

        $.ajax({
            url: apiUrl,
            type: 'GET',
            success: function (res) {
                const articlePage = res.data;
                renderTable(articlePage.content);
                renderPagination(articlePage);
            },
            error: function () {
                alert('게시글을 불러오는 데 실패했습니다.');
            }
        });
    }

    function renderTable(articles) {
        const tbody = $('#articleTableBody');
        tbody.empty();

        if (!articles || articles.length === 0) {
            tbody.append(`<tr><td colspan="4">게시글이 없습니다.</td></tr>`);
            return;
        }

        articles.forEach(article => {
            console.log("id:", article.id);
            console.log("title:", article.title);
            console.log("writer:", article.writer);
            console.log("writerId:", article.writerId);
            console.log("created:", article.formattedCreatedAt);

            const row =
                '<tr>' +
                '<td>' + article.id + '</td>' +
                '<td><a href="/api/v1/view/profile/view?memberId=' + article.writerId + '">' + article.writer + '</a></td>' +
                '<td><a class="article-link" href="/api/v1/view/articles/' + article.id + '">' + article.title + '</a></td>' +
                '<td>' + article.formattedCreatedAt + '</td>' +
                '</tr>';

            tbody.append(row);
        });
    }

    function renderPagination(pageInfo) {
        const pagination = $('#paginationContainer');
        pagination.empty();

        const current = pageInfo.number;
        const total = pageInfo.totalPages;

        const blockSize = 3;
        const start = Math.floor(current / blockSize) * blockSize;
        const end = Math.min(start + blockSize, total);

        // 처음
        pagination.append('<a href="?page=0">처음</a>');

        // 이전
        if (current > 0) {
            pagination.append('<a href="?page=' + (current - 1) + '">이전</a>');
        } else {
            pagination.append('<span>이전</span>');
        }

        // 페이지 번호
        for (let i = start; i < end; i++) {
            if (i === current) {
                pagination.append('<span class="active">' + (i + 1) + '</span>');
            } else {
                pagination.append('<a href="?page=' + i + '">' + (i + 1) + '</a>');
            }
        }

        // 다음
        if (current + 1 < total) {
            pagination.append('<a href="?page=' + (current + 1) + '">다음</a>');
        } else {
            pagination.append('<span>다음</span>');
        }

        // 마지막
        pagination.append('<a href="?page=' + (total - 1) + '">마지막</a>');
    }
</script>
</body>
</html>
