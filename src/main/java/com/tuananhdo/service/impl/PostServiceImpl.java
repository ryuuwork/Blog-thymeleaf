package com.tuananhdo.service.impl;

import com.tuananhdo.entity.Post;
import com.tuananhdo.entity.User;
import com.tuananhdo.mapper.PostMapper;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.repository.CommentRepository;
import com.tuananhdo.repository.PostRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.PostService;
import com.tuananhdo.utils.ReadTimeUtils;
import com.tuananhdo.utils.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    public static final int POSTS_SIZE_PAGE = 6;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public Page<PostDTO> findAllPostByPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, POSTS_SIZE_PAGE);
        Page<Post> post = postRepository.findAll(pageable);
        return post.map(PostMapper::mapToPostDTO);
    }

    @Override
    public Page<PostDTO> findPostsByUser(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, POSTS_SIZE_PAGE);
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User createdBy = userRepository.findByEmail(email);
        Page<Post> posts = postRepository.findPostByUser(createdBy, pageable);
        return posts.map(PostMapper::mapToPostDTO);
    }

    @Override
    public Page<PostDTO> findAllPostsWithCommentCountAndReadTime(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, POSTS_SIZE_PAGE);
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> {
            PostDTO postDTO = PostMapper.mapToPostDTO(post);
            postDTO.setCommentCount(getCommentCount(postDTO));
            postDTO.setReadTime(getReadTime(postDTO));
            LocalDateTime pastTime = post.getCreatedOn();
            LocalDateTime nowTime = LocalDateTime.now();
            String timePost = TimeUnit.getTimePost(pastTime, nowTime);
            postDTO.setTimeOfPost(timePost);
            return postDTO;
        });
    }


    @Override
    public List<PostDTO> searchPosts(String search) {
        List<Post> posts = postRepository.searchPosts(search);
        return posts.stream()
                .map(PostMapper::mapToPostDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findAllPosts() {
        List<Post> post = postRepository.findAll();
        return post.stream()
                .map(PostMapper::mapToPostDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Post savePost(PostDTO postDTO) {
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User user = userRepository.findByEmail(email);
        Post post = PostMapper.mapToPost(postDTO);
        post.setCreatedBy(user);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostDTO findByPostId(Long postId) {
        return postRepository.findById(postId)
                .map(PostMapper::mapToPostDTO)
                .orElse(null);
    }

    @Override
    public PostDTO findByPostUrl(String postUrl) {
        return postRepository.findByUrl(postUrl)
                .map(post -> {
                    PostDTO postDTO = PostMapper.mapToPostDTO(post);
                    postDTO.setCommentCount(getCommentCount(postDTO));
                    postDTO.setReadTime(getReadTime(postDTO));
                    return postDTO;
                }).orElse(null);
    }

    private Long getCommentCount(PostDTO postDTO) {
        return commentRepository.countCommentsByPostId(postDTO.getId());
    }

    private String getReadTime(PostDTO postDTO) {
        return ReadTimeUtils.calculateReadTime(postDTO.getContent());
    }
}
