package com.tuananhdo.service;

import com.tuananhdo.exception.PostNotFoundException;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.payload.PostDTO;

import java.util.List;

public interface PostService {

    List<PostDTO> findAllPosts();

    void findAllPostsWithCommentCountAndReadTime(int pageNumber,PagingAndSortingHelper helper);

    void findAllPostByPage(int pageNumber, PagingAndSortingHelper helper);

    void findPostsByUser(int pageNumber, PagingAndSortingHelper helper);

    PostDTO createOrUpdatePost(PostDTO postDTO) throws PostNotFoundException;

    PostDTO findByPostId(Long postId);

    void deletePost(Long postId);

    PostDTO findByPostUrl(String postUrl);

    List<PostDTO> searchPosts(String search);

}
