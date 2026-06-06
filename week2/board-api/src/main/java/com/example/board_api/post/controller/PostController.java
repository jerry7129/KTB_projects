package com.example.board_api.post.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.post.controller.dto.PostRequestDto;
import com.example.board_api.post.controller.dto.PostResponseDto;
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

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 업로드
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost (
            @AuthenticationPrincipal Long writerId,
            @Valid @RequestBody PostRequestDto requestDto
            ) {
        PostResponseDto responseDto = postService.createPost(writerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("CREATE_SUCCESS", "게시글 생성을 성공했습니다.", responseDto));
    }

    // 게시글 수정
    @PatchMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost (
            @AuthenticationPrincipal Long writerId,
            @Valid @RequestBody PostRequestDto requestDto
            ) {
        PostResponseDto responseDto = postService.updatePost(writerId, requestDto);
        return ResponseEntity.status((HttpStatus.OK))
                .body(ApiResponse.of("UPDATE_SUCCESS", "게시글 수정을 성공했습니다.", responseDto));
    }
}
