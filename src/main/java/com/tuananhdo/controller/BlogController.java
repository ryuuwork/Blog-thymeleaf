package com.tuananhdo.controller;

import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.service.PostService;
import com.tuananhdo.service.impl.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class BlogController {
    private final PostService postServices;

    @GetMapping("/")
    public String viewBlogPosts(Model model) {
        return listPostHomeByPage(1, model);
    }

    @GetMapping("/posts/page/{pageNumber}")
    public String listPostHomeByPage(@PathVariable("pageNumber") int pageNumber, Model model) {
        Page<PostDTO> posts = postServices.findAllPostsWithCommentCountAndReadTime(pageNumber);
        int startCount = (pageNumber - 1) * PostServiceImpl.POSTS_SIZE_PAGE + 1;
        long endCount = Math.min(startCount + PostServiceImpl.POSTS_SIZE_PAGE - 1, posts.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("posts", posts);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", posts.getTotalElements());
        model.addAttribute("totalPages", posts.getTotalPages());
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
