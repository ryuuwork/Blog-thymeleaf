package com.tuananhdo.controller;

import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.CommentService;
import com.tuananhdo.service.FileUploadService;
import com.tuananhdo.service.PostService;
import com.tuananhdo.service.impl.FileUploadServiceImpl;
import com.tuananhdo.utils.ROLE;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    private final CommentService commentService;
    private final FileUploadService fileUploadService;

    @GetMapping("/admin/posts")
    public String homeReal(Model model) {
        return listPostsByPage(1, model);
    }

    @GetMapping("/admin/posts/page/{pageNumber}")
    public String listPostsByPage(@PathVariable("pageNumber") int pageNumber, Model model) {
        String role = SecurityUtils.getRole();
        LOGGER.error(role);
        Page<PostDTO> posts;
        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            posts = postService.findAllPostByPage(pageNumber);
        } else {
            posts = postService.findPostsByUser(pageNumber);

        }
        int pageSize = 4;
        int startCount = (pageNumber - 1) * pageSize + 1;
        long endCount = Math.min(startCount + pageSize - 1, posts.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("posts", posts);
        model.addAttribute("postTitle", "Post");
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", posts.getTotalElements());
        model.addAttribute("totalPages", posts.getTotalPages());
        return "admin/home";
    }

    @GetMapping("/admin/posts/newpost")
    public String newFormPost(Model model) {
        PostDTO post = new PostDTO();
        model.addAttribute("post", post);
        return "admin/create-post";
    }

    @PostMapping("/admin/posts")
    public String savePost(@Valid @ModelAttribute("post") PostDTO postDTO, BindingResult result,
                           @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("post", postDTO);
            return "admin/create-post";
        }
        postDTO.setUrl(FileUploadServiceImpl.getUrl(postDTO.getTitle()));
        if (!multipartFile.isEmpty()) {
            fileUploadService.savePostWithFile(postDTO, multipartFile);
        } else {
            postService.savePost(postDTO);
        }
        return "redirect:/admin/posts";
    }


    @PostMapping("/admin/posts/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @Valid @ModelAttribute("post") PostDTO postDTO, BindingResult result,
                             @RequestParam("image") MultipartFile multipartFile, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("post", postDTO);
            return "admin/update-post";
        }
        if (!multipartFile.isEmpty()) {
            fileUploadService.savePostWithFile(postDTO, multipartFile);
        } else {
            fileUploadService.cleanUploadDir(postId);
            postService.savePost(postDTO);
        }
        model.addAttribute("post", postDTO);
        return "redirect:/admin/posts";
    }

    @GetMapping("/admin/posts/{postId}/edit")
    public String getPostById(@PathVariable("postId") Long postId, Model model) {
        PostDTO post = postService.findByPostId(postId);
        model.addAttribute("post", post);
        return "admin/update-post";
    }

    @GetMapping("/admin/posts/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId) throws IOException {
        postService.deletePost(postId);
        fileUploadService.cleanUploadDir(postId);
        return "redirect:/admin/posts";
    }

    @GetMapping("/admin/posts/{postUrl}/view")
    public String viewPost(@PathVariable("postUrl") String postUrl, Model model) {
        PostDTO post = postService.findByPostUrl(postUrl);
        CommentDTO comment = new CommentDTO();
        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        return "admin/single-post";
    }

    @GetMapping("/admin/posts/search")
    public String searchPosts(@RequestParam(value = "search") String search, Model model) {
        List<PostDTO> posts = postService.searchPosts(search);
        model.addAttribute("posts", posts);
        return "home1";
    }

    @GetMapping("/admin/posts/export/csv")
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<Void> exportPostsToCSV(HttpServletResponse reponse) throws IOException {
        List<PostDTO> posts = postService.findAllPosts();
        PostCsvExporter exporter = new PostCsvExporter();
        return exporter.export(posts, reponse);
    }

    @GetMapping("/admin/posts/export/excel")
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<Void> exportPostsToExcel(HttpServletResponse reponse) throws IOException {
        List<PostDTO> posts = postService.findAllPosts();
        PostExcelExporter exporter = new PostExcelExporter();
        return exporter.export(posts, reponse);
    }

    @GetMapping("/admin/posts/comments")
    public String postComments(Model model) {
        String role = SecurityUtils.getRole();
        List<CommentDTO> comments;
        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            comments = commentService.findAllComments();
        } else {
            comments = commentService.findCommentsByPost();
        }
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    @GetMapping("/admin/posts/comments/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/admin/posts/comments";
    }

}
