package com.tuananhdo.controller;

import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.paging.PaingAndSortingParam;
import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class BlogController {
    private final PostService postServices;

    @GetMapping("/")
    public String viewBlogPosts() {
        return "redirect:/posts/page/1";
    }

    @GetMapping("/posts/page/{pageNumber}")
    public String listPostHomeByPage(@PathVariable("pageNumber") int pageNumber,
                                     @PaingAndSortingParam(moduleURL = "/posts", listName = "posts", pageTitle = "Post")
                                     PagingAndSortingHelper helper) {
        postServices.findAllPostsWithCommentCountAndReadTime(pageNumber, helper);
        return "web/home";
    }

    @GetMapping("/posts/{postUrl}")
    public String postUrl(@PathVariable("postUrl") String postUrl, Model model) {
        PostDTO post = postServices.findByPostUrl(postUrl);
        CommentDTO comment = new CommentDTO();
        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        return "web/single-posts";
    }

    @GetMapping("/error")
    public String error() {
        return "/error/404";
    }

}
