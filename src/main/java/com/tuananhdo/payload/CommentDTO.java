package com.tuananhdo.payload;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    @NotEmpty(message = "Can not leave the name blank")
    private String name;
    @NotEmpty(message = "Can not leave the email blank")
    @Email
    private String email;
    @NotEmpty(message = "Can not leave the content blank")
    private String content;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private int votes;
}
