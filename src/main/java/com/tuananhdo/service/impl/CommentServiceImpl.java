package com.tuananhdo.service.impl;

import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.entity.Comment;
import com.tuananhdo.entity.Post;
import com.tuananhdo.entity.User;
import com.tuananhdo.mapper.CommentMapper;
import com.tuananhdo.repository.CommentRepository;
import com.tuananhdo.repository.PostRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.service.CommentService;
import com.tuananhdo.security.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Override
    public List<CommentDTO> findCommentsByPost() {
        String email = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUsername();
        User createdBy = userRepository.findByEmail(email);
        Long userId = createdBy.getId();
        List<Comment> comments = commentRepository.findCommentsByPost(userId);
        return comments.stream()
                .map(CommentMapper::mapToCommentDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<CommentDTO> findAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(CommentMapper::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createComment(String postUrl, CommentDTO commentDTO) {
        Post post = postRepository.findByUrl(postUrl).orElse(null);
        Comment comment = CommentMapper.mapToComment(commentDTO);
        comment.setPost(post);
        commentRepository.save(comment);
    }

    @Override
    public CommentDTO findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .map(CommentMapper::mapToCommentDTO)
                .orElse(null);
    }

    @Override
    public List<CommentDTO> findByPostId(Long id) {
        List<Comment> comment = commentRepository.findByPostId(id);
        return comment.stream().map(CommentMapper::mapToCommentDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Long countCommentsByPostId(Long postId) {
        return commentRepository.countCommentsByPostId(postId);
    }
}
