package com.tuananhdo.controller;

import com.tuananhdo.exception.PostNotFoundException;
import com.tuananhdo.paging.PagingAndSortingHelper;
import com.tuananhdo.paging.PaingAndSortingParam;
import com.tuananhdo.payload.AccountDTO;
import com.tuananhdo.payload.CommentDTO;
import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.security.SecurityUtils;
import com.tuananhdo.service.AccountService;
import com.tuananhdo.service.CommentService;
import com.tuananhdo.service.PostService;
import com.tuananhdo.utils.FileUploadUtil;
import com.tuananhdo.utils.ROLE;
import com.tuananhdo.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);
    public static final String DEFAULT_REDIRECT_URL = "redirect:/admin/posts/page/1?sortField=title&sortDir=asc";

    private final PostService postService;
    private final CommentService commentService;
    private final AccountService accountService;

    @GetMapping("/admin/posts")
    public String homeAllPosts() {
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/admin/posts/page/{pageNumber}")
    public String listPostsByPage(@PathVariable("pageNumber") int pageNumber,
                                  @PaingAndSortingParam(moduleURL = "/admin/posts",
                                          listName = "posts",pageTitle = "Management Post")
                                  PagingAndSortingHelper helper,Model model) {
        AccountDTO loggedUser = accountService.getLoggedAccount();
        model.addAttribute("loggedUser", loggedUser);
        String role = SecurityUtils.getRole();
        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            postService.findPostsByUser(pageNumber, helper);
        } else {
            postService.findAllPostByPage(pageNumber, helper);
        }
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
                           @RequestParam("image") MultipartFile multipartFile,
                           Model model,
                           RedirectAttributes redirectAttributes) throws IOException, PostNotFoundException {
        if (result.hasErrors()) {
            model.addAttribute("post", postDTO);
            return "admin/create-post";
        }
        postDTO.setUrl(StringUtil.getUrl(postDTO.getTitle()));
        if (!multipartFile.isEmpty()) {
            String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
            postDTO.setPhotos(fileName);
            PostDTO savePost = postService.createOrUpdatePost(postDTO);
            String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.POST_PHOTOS, savePost.getId());
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            postService.createOrUpdatePost(postDTO);
        }
        redirectAttributes.addFlashAttribute("message", "The post " + StringUtil.toLowerCase(postDTO.getTitle()) + " has been updated successfully!");
        return redirectPageUrlForTitleOfPost(postDTO);
    }

    private static String redirectPageUrlForTitleOfPost(PostDTO postDTO) {
        String title = postDTO.getTitle();
        String getTitlePost = title.contains(" ") ? title.split(" ")[0] : title;
        return "redirect:/admin/posts/page/1?sortField=id&sortDir=asc&keyword=" + getTitlePost;
    }


    @PostMapping("/admin/posts/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @Valid @ModelAttribute("post") PostDTO postDTO, BindingResult result,
                             @RequestParam("image") MultipartFile multipartFile,
                             Model model,
                             RedirectAttributes redirectAttributes) throws IOException, PostNotFoundException {
        if (result.hasErrors()) {
            model.addAttribute("post", postDTO);
            return "admin/update-post";
        }
        updatePostDetails(postId, postDTO, multipartFile);
        PostDTO updatedPost = postService.createOrUpdatePost(postDTO);
        redirectAttributes.addFlashAttribute("message", "The post " + StringUtil.toLowerCase(updatedPost.getTitle()) + " has been updated successfully!");
        return DEFAULT_REDIRECT_URL;
    }

    private static void updatePostDetails(Long postId, PostDTO postDTO, MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = FileUploadUtil.getOriginalFileName(multipartFile);
            postDTO.setPhotos(fileName);
            String uploadDir = FileUploadUtil.getPhotoFolderId(FileUploadUtil.POST_PHOTOS, postId);
            FileUploadUtil.saveFileAndCleanOldImage(multipartFile, fileName, uploadDir);
        }
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
        String folderImage = FileUploadUtil.getPhotoFolderId(FileUploadUtil.POST_PHOTOS,postId);
        FileUploadUtil.cleanDir(folderImage);
        return DEFAULT_REDIRECT_URL;
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
        return DEFAULT_REDIRECT_URL;
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
        AccountDTO loggedUser = accountService.getLoggedAccount();
        String role = SecurityUtils.getRole();
        List<CommentDTO> comments;
        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            comments = commentService.findAllComments();
        } else {
            comments = commentService.findCommentsByPost();
        }
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    @GetMapping("/admin/posts/comments/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/admin/posts/comments";
    }

}
