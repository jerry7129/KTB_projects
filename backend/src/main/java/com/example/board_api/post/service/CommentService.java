package com.example.board_api.post.service;

import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.post.controller.dto.CommentRequestDto;
import com.example.board_api.post.controller.dto.CommentResponseDto;
import com.example.board_api.post.controller.dto.CommentWriterResponseDto;
import com.example.board_api.post.domain.CommentRepository;
import com.example.board_api.post.domain.PostRepository;
import com.example.board_api.post.domain.entity.Comment;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto createComment(Integer userId, Long postId, CommentRequestDto requestDto) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        Comment comment = Comment.builder()
                .content(requestDto.getCommentContent())
                .build();
        
        comment.setWriter(writer);
        comment.setPost(post);

        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                CommentWriterResponseDto.from(writer),
                (long) post.getPostComments().size(),
                comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null,
                comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null
        );
    }
}
