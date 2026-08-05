package com.example.board_api.post.service;

import com.example.board_api.file.domain.entity.PostImage;
import com.example.board_api.file.service.FileService;
import com.example.board_api.global.exception.AuthorizedException;
import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.post.controller.dto.PostRequestDto;
import com.example.board_api.post.controller.dto.PostResponseDto;
import com.example.board_api.post.controller.dto.PostListCursorResponseDto;
import com.example.board_api.post.controller.dto.LikeResponseDto;
import com.example.board_api.post.domain.PostRepository;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.domain.entity.PostLike;
import com.example.board_api.post.domain.entity.PostLikeEntityId;
import com.example.board_api.post.domain.PostLikeRepository;
import com.example.board_api.post.domain.entity.PostStatus;
import com.example.board_api.post.domain.PostStatusRepository;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;

import com.example.board_api.file.repository.PostImageRepository;
import com.example.board_api.global.util.FileUtil;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final FileService fileService;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostStatusRepository postStatusRepository;
    private final PostViewCountService postViewCountService;
    private final PostImageRepository postImageRepository;

    // 게시글 이미지 첨부를 위해서는 게시글 생성 전에 이미지를 먼저 저장해서 임시 주소를 발급 받아야함.
    @Transactional
    public PostResponseDto createPost(Integer writerId, PostRequestDto requestDto) {
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        // post 생성
        Post post = Post.builder()
                .title(requestDto.getPostTitle())
                .content(requestDto.getPostContent())
                .writer(writer)
                .build();

        // post 부가 정보 생성
        PostStatus postStatus = PostStatus.builder().build();

        // 연관 관계 편의 메소드로 post와 postStatus를 연결
        postStatus.setPost(post);

        // DB에는 post만 저장함.
        // -> cascade와 mapsId 로 인해 postStatus도 post와 같은 key를 가지면서 DB에 자동으로 저장됨.
        postRepository.save(post);

        // POST /users/temp-profile-image 요청을 통해 얻은 임시 저장소 URL을 바탕으로
        // POST /users 로 유저 생성 이후에 임시 저장소의 프로필 이미지를 profile/{userId}/{fileName} 으로 옮김.
        // dirty check 로 유저 엔티티 변동이 자동으로 DB 반영됨
        if (requestDto.getPostImageUrl() != null
                && !requestDto.getPostImageUrl().isBlank()
                // 프로필 이미지가 default-profile.png 로 설정되어 있어도 무시.
                && !requestDto.getPostImageUrl().contains("default-profile.png")) {
            PostImage movedFile = fileService.moveTempToPost(requestDto.getPostImageUrl(), post.getId());
            post.changePostImage(movedFile); // post 엔티티에 postImage 연결
        }

        return PostResponseDto.from(post, false);
    }

    @Transactional
    public PostResponseDto updatePostInfo(
            Integer writerId, Long postId,
            PostRequestDto requestDto
    ) {
        Post post = getPostWithAuthorization(writerId, postId);

        PostImage newImage = null;
        if (requestDto.getPostImageUrl() != null && !requestDto.getPostImageUrl().isBlank()) {
            newImage = resolvePostImage(requestDto.getPostImageUrl());
        }

        // 이미지를 디스크에 저장 - 현재는 local 서버의 /uploads 폴더에 저장 중
        // 변경 시, 기존에 가지고 있던 물리 파일을 삭제. (고아 파일 방지)
        boolean isImageChanged = false;
        if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
            PostImage oldImage = post.getPostImages().getFirst();
            if (newImage == null || !oldImage.getFileKey().equals(newImage.getFileKey())) {
                fileService.delete(oldImage.getFileKey());
                isImageChanged = true;
            }
        } else if (newImage != null) {
            isImageChanged = true;
        }

        // JPA의 dirty check를 사용. Transaction 종료 후에 자동으로 DB에 commit 됨.
        // changePostInformation 안에서 postImages.clear()가 호출되어 기존 DB 데이터가 고아 객체로 지워짐.
        if (isImageChanged) {
            post.changePostInformation(requestDto.getPostTitle(), requestDto.getPostContent(), newImage);
        } else {
            post.changePostInformation(requestDto.getPostTitle(), requestDto.getPostContent(), 
                    (post.getPostImages() != null && !post.getPostImages().isEmpty()) ? post.getPostImages().getFirst() : null);
        }
        return PostResponseDto.from(post, false);
    }

    private PostImage resolvePostImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        String fileKey = FileUtil.toFileKey(imageUrl);

        return postImageRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new NotFoundException("POST_IMAGE_NOT_FOUND"));
    }

    @Transactional
    public void deletePost(Integer writerId, Long postId) {
        Post post = getPostWithAuthorization(writerId, postId);

        // 게시글 물리 이미지 파일 삭제
        if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
            for (PostImage postImage : post.getPostImages()) {
                fileService.delete(postImage.getFileKey());
            }
        }

        // cascade 옵션 때문에 postStatus도 같이 삭제됨.
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public PostListCursorResponseDto getPosts(String keyword, String sort, String order, Long cursor, int limit) {
        List<Post> posts = postRepository.findPostsWithCursor(keyword, sort, order, cursor, PageRequest.of(0, limit));

        boolean hasNext = false;
        if (posts.size() > limit) {
            hasNext = true;
            posts.remove(limit);
        }

        Long nextCursor = hasNext ? posts.getLast().getId() : null;

        List<PostResponseDto> postList = posts.stream()
                .map(post -> PostResponseDto.from(post, false))
                .collect(Collectors.toList());

        return PostListCursorResponseDto.builder()
                .posts(postList)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public PostResponseDto getPost(Long postId, Integer userId) {
        postViewCountService.incrementViewCount(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        boolean isLiked = false;
        if (userId != null) {
            isLiked = postLikeRepository.existsById(new PostLikeEntityId(userId, postId));
        }

        return PostResponseDto.from(post, isLiked);
    }

    @Transactional
    public LikeResponseDto addLike(Integer userId, Long postId) {
        if (postLikeRepository.existsById(new PostLikeEntityId(userId, postId))) {
            // 이미 좋아요를 누른 경우
            return new LikeResponseDto(postStatusRepository.getLikeCount(postId));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        // likes table에 좋아요를 누른 유저랑 게시글 아이디를 저장
        postLikeRepository.save(postLike);
        // postStatus의 likeCount를 증가
        postStatusRepository.incrementLikeCount(postId);

        return new LikeResponseDto(postStatusRepository.getLikeCount(postId));
    }

    @Transactional
    public LikeResponseDto removeLike(Integer userId, Long postId) {
        PostLikeEntityId id = new PostLikeEntityId(userId, postId);
        postLikeRepository.findById(id).ifPresent(postLike -> {
            // likes table에 좋아요를 누른 유저랑 게시글 아이디를 제거
            postLikeRepository.delete(postLike);
            // postStatus의 likeCount를 감소
            postStatusRepository.decrementLikeCount(postId);
        });

        return new LikeResponseDto(postStatusRepository.getLikeCount(postId));
    }

    // ========= private method ===========

    // 게시글 조회 및 작성자 권한 검증을 동시에 수행하는 공통 메서드
    private Post getPostWithAuthorization(Integer writerId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));
        
        if (!post.getWriter().getId().equals(writerId)) {
            throw new AuthorizedException("권한이 없습니다.");
        }
        return post;
    }
}
