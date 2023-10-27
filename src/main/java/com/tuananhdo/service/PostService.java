package com.tuananhdo.service;

import com.tuananhdo.entity.Post;
import com.tuananhdo.payload.PostDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    List<PostDTO> findAllPosts();

    Page<PostDTO> findAllPostsWithCommentCountAndReadTime(int pageNumber);

    Page<PostDTO> findAllPostByPage(int pageNumber);

    Page<PostDTO> findPostsByUser(int pageNumber);

    Post savePost(PostDTO postDTO);

    PostDTO findByPostId(Long postId);

    void deletePost(Long postId);

    PostDTO findByPostUrl(String postUrl);

    List<PostDTO> searchPosts(String search);

}
