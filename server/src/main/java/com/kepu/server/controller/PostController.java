package com.kepu.server.controller;

import com.kepu.server.dto.ResponseResult;
import com.kepu.server.entity.Post;
import com.kepu.server.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 创建文章
     */
    @PostMapping("/create")
    public ResponseResult createPost(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            
            Post post = new Post();
            post.setTitle(params.get("title").toString());
            post.setContent(params.get("content").toString());
            post.setLocation(params.get("location").toString());
            
            if (params.get("longitude") != null) {
                post.setLongitude(Double.parseDouble(params.get("longitude").toString()));
            }
            if (params.get("latitude") != null) {
                post.setLatitude(Double.parseDouble(params.get("latitude").toString()));
            }
            
            Post createdPost = postService.createPost(post, userId);
            return ResponseResult.success("文章发布成功", createdPost);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/detail")
    public ResponseResult getPostDetail(@RequestParam Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseResult.success(post);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/update")
    public ResponseResult updatePost(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            Long postId = Long.parseLong(params.get("postId").toString());
            
            Post post = new Post();
            post.setId(postId);
            
            if (params.get("title") != null) {
                post.setTitle(params.get("title").toString());
            }
            if (params.get("content") != null) {
                post.setContent(params.get("content").toString());
            }
            if (params.get("location") != null) {
                post.setLocation(params.get("location").toString());
            }
            if (params.get("longitude") != null) {
                post.setLongitude(Double.parseDouble(params.get("longitude").toString()));
            }
            if (params.get("latitude") != null) {
                post.setLatitude(Double.parseDouble(params.get("latitude").toString()));
            }
            
            Post updatedPost = postService.updatePost(post, userId);
            return ResponseResult.success("文章更新成功", updatedPost);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/delete")
    public ResponseResult deletePost(@RequestParam Long postId, @RequestParam Long userId) {
        try {
            postService.deletePost(postId, userId);
            return ResponseResult.success("文章删除成功", null);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public ResponseResult getPostList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            Map<String, Object> result = postService.getPostList(page, pageSize, keyword);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取用户文章列表
     */
    @GetMapping("/user")
    public ResponseResult getUserPosts(
            @RequestParam Long userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = postService.getUserPosts(userId, page, pageSize);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 按位置搜索文章
     */
    @GetMapping("/search/location")
    public ResponseResult searchPostsByLocation(
            @RequestParam String location,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = postService.searchPostsByLocation(location, page, pageSize);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
} 