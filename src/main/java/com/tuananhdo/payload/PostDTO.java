package com.tuananhdo.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;
    @NotBlank(message = "{post.title.not.blank}")
    private String title;
    private String url;
    @NotBlank(message = "{post.content.not.blank}")
    private String content;
    @NotBlank(message = "{post.description.not.blank}")
    private String shortDescription;
    private String photos;
    private LocalDateTime createdOn;
    private LocalDateTime updateOn;
    private Long commentCount;
    private String readTime;
    private String timeOfPost;
    private List<CommentDTO> comments;
    @Transient
    public String getPhotosPath() {
        if (id == null || photos == null || photos.isEmpty()) {
            return "/common/assets/images/products/s1.jpg";
        }
        return "/post-photos/" + this.id + "/" + this.photos;
    }
}
