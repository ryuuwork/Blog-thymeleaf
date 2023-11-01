package com.tuananhdo.mapper;

import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.entity.Post;

import java.util.stream.Collectors;

public class PostMapper {

    public static PostDTO mapToPostDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .shortDescription(post.getShortDescription())
                .url(post.getUrl())
                .photos(post.getPhotos())
                .createdOn(post.getCreatedOn())
                .updateOn(post.getUpdatedOn())
                .build();
    }

    public static Post mapToPost(PostDTO postDTO) {
        return Post.builder()
                .id(postDTO.getId())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .shortDescription(postDTO.getShortDescription())
                .url(postDTO.getUrl())
                .photos(postDTO.getPhotos())
                .createdOn(postDTO.getCreatedOn())
                .updatedOn(postDTO.getUpdateOn())
                .build();
    }

}
