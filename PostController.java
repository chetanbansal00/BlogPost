package io.mountblue.Blogproject.controllers;

import io.mountblue.Blogproject.entity.Comment;
import io.mountblue.Blogproject.entity.Post;
import io.mountblue.Blogproject.entity.Tag;
import io.mountblue.Blogproject.repository.PostTagRepository;
import io.mountblue.Blogproject.repository.TagRepository;
import io.mountblue.Blogproject.service.PostService;
import io.mountblue.Blogproject.service.PostServiceImpl;
import io.mountblue.Blogproject.service.PostTagService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {
        private final PostService postService;
        private final TagRepository tagRepository;
        private final PostTagService postTagService;

        public PostController(PostService postService, TagRepository tagRepository, PostTagService postTagService){
            this.postService = postService;
            this.tagRepository = tagRepository;
            this.postTagService = postTagService;
        }

        @GetMapping("/createPost")
        public String createPost(Model theModel){
            Post post = new Post();
            theModel.addAttribute("post",post);
            return "createpost";
        }

        @PostMapping("/savePost")
        @Transactional
        public String savePost(@ModelAttribute("post") Post post,
                               @RequestParam("tags") String tags){
            postService.save(post,tags);
            return "redirect:/showPosts";
        }

        @GetMapping("/showPosts")
        public String showPosts(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model theModel){
            // Get paginated posts
            Page<Post> postPage = postService.getPaginatedPosts(page, size);

            // Add attributes for rendering the posts and pagination controls
            theModel.addAttribute("postPage", postPage);
            theModel.addAttribute("currentPage", page);
            theModel.addAttribute("totalPages", postPage.getTotalPages());

//            List<Post> posts = postService.getAllPosts();
//            theModel.addAttribute("allposts",posts);
            return "showpost";
        }

        @GetMapping("/fullpost/{id}")
        public String fullPost(Model theModel , @PathVariable Long id){
            Post post = postService.getPost(id);
            theModel.addAttribute("post",post);

            theModel.addAttribute("tempComment",new Comment());
            theModel.addAttribute("allComments",post.getComments());
            return "fullpost";
        }

        @GetMapping("/deletePost/{id}")
        @Transactional
        public String deletePost(@PathVariable Long id){
            postService.deletePost(id);
            return "redirect:/showPosts";
        }

        @GetMapping("/updatePost/{id}")
        @Transactional
        public String updatePost(@PathVariable Long id, Model theModel){
            Post post = postService.getPost(id);
            List<Tag> tagList = postTagService.getTagByPostId(id);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tagList.size(); i++) {
                sb.append(tagList.get(i).getName());
                if (i < tagList.size() - 1) {
                    sb.append(",");
                }
            }
            String tags = sb.toString();

            theModel.addAttribute("tags",tags);
            theModel.addAttribute("post",post);
            return "createpost";
        }

        @PostMapping("/updatePost")
        @Transactional
        public String updatePost(@ModelAttribute("post") Post post,
                                 @RequestParam("tags") String tags){
            postService.updatePost(post,tags);
            return "redirect:/showPosts";
        }
}
