package com.tuananhdo.mapper;

import com.tuananhdo.entity.Comment;
import com.tuananhdo.payload.CommentDTO;

public class CommentMapper {

    public static CommentDTO mapToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .name(comment.getName())
                .email(comment.getEmail())
                .content(comment.getContent())
                .votes(comment.getVotes())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .build();
    }

    public static Comment mapToComment(CommentDTO commentDTO) {
        return Comment.builder()
                .id(commentDTO.getId())
                .name(commentDTO.getName())
                .email(commentDTO.getEmail())
                .content(commentDTO.getContent())
                .votes(commentDTO.getVotes())
                .createdOn(commentDTO.getCreatedOn())
                .updatedOn(commentDTO.getUpdatedOn())
                .build();
    }
}
