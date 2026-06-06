package com.example.board_api.post.service;

import com.example.board_api.file.domain.entity.PostImage;
import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.file.service.FileService;
import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.post.controller.dto.PostRequestDto;
import com.example.board_api.post.controller.dto.PostResponseDto;
import com.example.board_api.post.domain.PostRepository;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.domain.entity.PostStatus;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    // 게시글 이미지 첨부를 위해서는 게시글 생성 전에 이미지를 먼저 저장해서 임시 주소를 발급 받아야함.
    @Transactional
    public PostResponseDto createPost(Long writerId, PostRequestDto requestDto) {
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

        return PostResponseDto.from(post, postStatus);
    }

    @Transactional
    public PostResponseDto updatePost(Long writerId, PostRequestDto requestDto) {
        return null;
    }
}
