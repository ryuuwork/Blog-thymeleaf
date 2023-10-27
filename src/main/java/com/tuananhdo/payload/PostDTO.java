package com.tuananhdo.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;
    @NotEmpty(message = "Can not leave the title blank")
    private String title;
    private String url;
    @NotEmpty(message = "Can not leave the content blank")
    private String content;
    @NotEmpty(message = "Can not leave the description blank")
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
