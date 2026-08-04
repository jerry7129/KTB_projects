package com.example.board_api.post.service;

import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.global.exception.AuthorizedException;
import com.example.board_api.post.controller.dto.CommentListCursorResponseDto;
import com.example.board_api.post.controller.dto.CommentRequestDto;
import com.example.board_api.post.controller.dto.CommentResponseDto;
import com.example.board_api.post.controller.dto.CommentWriterResponseDto;
import com.example.board_api.post.domain.CommentRepository;
import com.example.board_api.post.domain.PostRepository;
import com.example.board_api.post.domain.entity.Comment;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.domain.PostStatusRepository;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostStatusRepository postStatusRepository;

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
        
        postStatusRepository.incrementCommentCount(postId);

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                CommentWriterResponseDto.from(writer),
                postStatusRepository.getCommentCount(postId),
                comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null,
                comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null
        );
    }

    @Transactional(readOnly = true)
    public CommentListCursorResponseDto getComments(Long postId, Long startingAfter, Integer limit) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        List<Comment> comments;
        if (startingAfter == null) {
            comments = commentRepository.findByPostIdOrderByIdAsc(postId, PageRequest.of(0, limit + 1));
        } else {
            comments = commentRepository.findByPostIdAndIdGreaterThanOrderByIdAsc(postId, startingAfter, PageRequest.of(0, limit + 1));
        }

        boolean hasNext = false;
        if (comments.size() > limit) {
            hasNext = true;
            comments.remove(limit.intValue());
        }

        Long nextCursor = hasNext ? comments.getLast().getId() : null;

        List<CommentResponseDto> commentList = comments.stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        CommentWriterResponseDto.from(comment.getWriter()),
                        -1L,
                        comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null,
                        comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null
                ))
                .collect(Collectors.toList());

        return CommentListCursorResponseDto.builder()
                .comments(commentList)
                .startingAfter(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public void deleteComment(Integer userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        if (!comment.getWriter().getId().equals(userId)) {
            throw new AuthorizedException("권한이 없습니다.");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글의 댓글이 아닙니다.");
        }

        commentRepository.delete(comment);
        postStatusRepository.decrementCommentCount(postId);
    }

    @Transactional
    public CommentResponseDto updateComment(Integer userId, Long postId, Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        if (!comment.getWriter().getId().equals(userId)) {
            throw new AuthorizedException("권한이 없습니다.");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글의 댓글이 아닙니다.");
        }

        comment.changeComment(requestDto.getCommentContent());

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                CommentWriterResponseDto.from(comment.getWriter()),
                postStatusRepository.getCommentCount(postId),
                comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null,
                comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null
        );
    }
}
