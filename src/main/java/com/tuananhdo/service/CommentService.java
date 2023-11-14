package com.tuananhdo.service;

import com.tuananhdo.payload.CommentDTO;

import java.util.List;

public interface CommentService {

    void createComment(String postUrl, CommentDTO commentDTO);
    List<CommentDTO> findAllComments();
    void deleteComment(Long id);

    List<CommentDTO> findCommentsByPost();

    Long countCommentsByPostId(Long postId);

    CommentDTO findCommentById(Long commentId);

    List<CommentDTO> findByPostId(Long id);
}
