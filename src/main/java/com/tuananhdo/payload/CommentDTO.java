package com.tuananhdo.payload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    @NotBlank(message = "{comment.name.not.blank}")
    private String name;
    @NotBlank(message = "{comment.email.not.blank}")
    @Email
    private String email;
    @NotBlank(message = "{comment.email.not.blank}")
    private String content;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private int votes;
}
