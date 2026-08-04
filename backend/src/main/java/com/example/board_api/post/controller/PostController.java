package com.example.board_api.post.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.post.controller.dto.*;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.service.PostService;
import com.example.board_api.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    // 게시글 업로드
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost (
            @AuthenticationPrincipal Integer writerId,
            @Valid @RequestBody PostRequestDto requestDto
            ) {
        PostResponseDto responseDto = postService.createPost(writerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("CREATE_SUCCESS", "게시글 생성을 성공했습니다.", responseDto));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost (
            @AuthenticationPrincipal Integer writerId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequestDto requestDto
            ) {
        PostResponseDto responseDto = postService.updatePostInfo(writerId, postId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_SUCCESS", "게시글 수정을 성공했습니다.", responseDto));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost (
            @AuthenticationPrincipal Integer writerId,
            @PathVariable("postId") Long postId
    ) {
        postService.deletePost(writerId, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("DELETE_SUCCESS", "게시글 삭제를 성공했습니다.", null));
    }


    // 게시글 목록 조회 (커서 기반 paging)
    @GetMapping
    public ResponseEntity<ApiResponse<PostListCursorResponseDto>> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Long startingAfter,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        PostListCursorResponseDto responseDto = postService.getPosts(keyword, sort, order, startingAfter, limit);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.of("GET_SUCCESS", "게시글 목록 조회를 성공했습니다.", responseDto));
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId
    ) {
        PostResponseDto responseDto = postService.getPost(postId, userId);
        return ResponseEntity.ok(ApiResponse.of("GET_SUCCESS", "게시글 목록 조회를 성공했습니다.", responseDto));
    }

    // 특정 게시글 좋아요수 증가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDto>> addLike(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId
    ) {
        LikeResponseDto responseDto = postService.addLike(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("LIKE_SUCCESS", "게시글 좋아요를 성공했습니다.", responseDto));
    }

    // 특정 게시글 좋아요수 감소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDto>> removeLike(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId
    ) {
        LikeResponseDto responseDto = postService.removeLike(userId, postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("UNLIKE_SUCCESS", "게시글 좋아요 취소를 성공했습니다.", responseDto));
    }

    // 특정 게시글 댓글 생성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> addComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.createComment(userId, postId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("CREATE_SUCCESS", "댓글 생성을 성공했습니다.", responseDto));
    }

    // 특정 게시글 댓글 목록 조회 (커서 기반 paging)
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentListCursorResponseDto>> getComments(
            @PathVariable("postId") Long postId,
            @RequestParam(required = false) Long startingAfter,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        CommentListCursorResponseDto responseDto = commentService.getComments(postId, startingAfter, limit);
        return ResponseEntity.ok(ApiResponse.of("GET_SUCCESS", "댓글 목록 조회를 성공했습니다.", responseDto));
    }

    // 특정 게시글 댓글 수정
    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        CommentResponseDto responseDto = commentService.updateComment(userId, postId, commentId, requestDto);
        return ResponseEntity.ok(ApiResponse.of("UPDATE_SUCCESS", "댓글 수정을 성공했습니다.", responseDto));
    }

    // 특정 게시글 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("DELETE_SUCCESS", "댓글 삭제를 성공했습니다.", null));
    }
}
