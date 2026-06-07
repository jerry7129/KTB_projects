package com.example.board_api.post.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.post.controller.dto.PostRequestDto;
import com.example.board_api.post.controller.dto.PostResponseDto;
import com.example.board_api.post.controller.dto.PostListCursorResponseDto;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.service.PostService;
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
            @Valid @RequestPart("data") PostRequestDto requestDto,
            @RequestPart("postImage")MultipartFile postImage
            ) {
        PostResponseDto responseDto = postService.updatePostInfo(writerId, postId, requestDto, postImage);
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
            @RequestParam(required = false) Long startingAfter,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        PostListCursorResponseDto responseDto = postService.getPosts(startingAfter, limit);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.of("GET_SUCCESS", "게시글 목록 조회를 성공했습니다.", responseDto));
    }

    // 특정 게시글 조지
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable("postId") Long postId
    ) {
        PostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.of("GET_SUCCESS", "게시글 목록 조회를 성공했습니다.", responseDto));
    }

    // 특정 게시글 좋아요수 증가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> addLike(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId
    ) {
        postService.addLike(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("LIKE_SUCCESS", "게시글 좋아요를 성공했습니다.", null));
    }

    // 특정 게시글 좋아요수 감소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @AuthenticationPrincipal Integer userId,
            @PathVariable("postId") Long postId
    ) {
        postService.removeLike(userId, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("UNLIKE_SUCCESS", "게시글 좋아요 취소를 성공했습니다.", null));
    }
}
