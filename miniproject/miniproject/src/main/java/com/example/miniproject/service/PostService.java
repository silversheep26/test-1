package com.example.miniproject.service;

import com.example.miniproject.dto.CommentResponseDto;
import com.example.miniproject.dto.PostRequestDto;
import com.example.miniproject.dto.PostResponseDto;
import com.example.miniproject.entity.Comment;
import com.example.miniproject.entity.Post;
import com.example.miniproject.entity.User;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.repository.CommentRepository;
import com.example.miniproject.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

//    // 게시글 전체 조회
//    public List<PostResponseDto> getPostList(){
//        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
//        return postList.stream().map(PostResponseDto::new).collect(Collectors.toList());
//    }

    // 게시글 작성
    public PostResponseDto writePost(PostRequestDto postRequestDto, User user) {
        // 게시글 저장
        Post post = postRepository.saveAndFlush(new Post(postRequestDto,user));
        return new PostResponseDto(post);
    }

    //게시글 하나 조회
    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
        return new PostResponseDto(post);
    }

    // 게시물 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );
        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(post.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        post.updatePost(postRequestDto);
        return new PostResponseDto(post);
    }

    // 게시물 삭제
    @Transactional
    public String deletePost(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
        );

        if (user.getRole() != UserRoleEnum.ADMIN && !StringUtils.equals(post.getUser().getId(), user.getId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        postRepository.deleteById(postId);
        return "삭제 완료";
    }

}

//        // 파일 저장 경로
//        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
//MultipartFile file,
//        UUID uuid = UUID.randomUUID();  // 식별자 랜덤 생성
//
//        String fileName = uuid + "_" + file.getOriginalFilename();  // 랜덤식별자_본래파일명
//
//        File saveFile = new File(projectPath, fileName);
//
//        postRequestDto.setFilename(fileName);
//        postRequestDto.setFilepath("/files/" + fileName);  // 저장된 파일경로와 파일 이름
//
//        file.transferTo(saveFile);


//        List<Comment> commentList = commentRepository.findAll();
//        List<CommentResponseDto> comments = new ArrayList<>();
//        for (Comment comment : commentList) {
//            if (Objects.equals(comment.getPost().getId(), postId)) {
//                comments.add(new CommentResponseDto(comment));
//            }
//        }