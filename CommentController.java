package io.mountblue.Blogproject.controllers;

import io.mountblue.Blogproject.entity.Comment;
import io.mountblue.Blogproject.entity.Post;
import io.mountblue.Blogproject.service.CommentService;
import io.mountblue.Blogproject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {
    private CommentService commentService;
    private PostService postService;

    @Autowired
    CommentController(CommentService commentService, PostService postService){
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/{id}/saveComment")
    public String saveComment(@ModelAttribute("tempComment") Comment comment,
                              @PathVariable Long id){
        Post post = postService.getPost(id);
        comment.setPost(post);
        Comment savedComment = commentService.save(comment);
        post.addComments(comment);
        postService.save(post);
        return "redirect:/showPosts";
    }

    @GetMapping("/deleteComment/{id}")
    public String deleteComment(@PathVariable Long id){
        commentService.delete(id);
        return "redirect:/showPosts";
    }

    @GetMapping("/updateComment/{id}")
    public String updateComment(Model theModel, @PathVariable Long id){
        Comment commentobj = commentService.getComment(id);
        Post post = commentobj.getPost();

        theModel.addAttribute("post",post);
        theModel.addAttribute("commentobj", commentobj);
        theModel.addAttribute("allComments", post.getComments());
        theModel.addAttribute("tempComment", commentobj);
        return "fullpost";
    }

    @PostMapping("/updateComment/{id}")
    public String updateComment(@ModelAttribute("commentobj") Comment comment){
        commentService.update(comment.getId(), comment);
        return "redirect:/showPosts";
    }
}
