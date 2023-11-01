package com.tuananhdo.controller;

import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.service.CommentService;
import com.tuananhdo.service.PostService;
import com.tuananhdo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/{postUrl}/comments")
    public String createComment(@PathVariable("postUrl") String postUrl,
                                @Valid @ModelAttribute("comment") CommentDTO commentDTO, BindingResult result, Model model) {
        PostDTO post = postService.findByPostUrl(postUrl);
        if (result.hasErrors()) {
            model.addAttribute("comment", commentDTO);
            model.addAttribute("post", post);
            return "/web/single-posts";
        }
        commentService.createComment(postUrl, commentDTO);
        return "redirect:/posts/" + postUrl;
    }

}
