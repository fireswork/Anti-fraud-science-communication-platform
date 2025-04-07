package com.kepu.server.controller;

import com.kepu.server.dto.ResponseResult;
import com.kepu.server.entity.Comment;
import com.kepu.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public ResponseResult addComment(@RequestBody Map<String, Object> params) {
        try {
            Long postId = Long.parseLong(params.get("postId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            String content = params.get("content").toString();
            
            Comment comment = new Comment();
            comment.setContent(content);
            
            Comment savedComment = commentService.addComment(comment, postId, userId);
            return ResponseResult.success("评论成功", savedComment);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取文章评论列表
     */
    @GetMapping("/list")
    public ResponseResult getPostComments(@RequestParam Long postId) {
        try {
            List<Comment> comments = commentService.getPostComments(postId);
            return ResponseResult.success(comments);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 分页获取文章评论
     */
    @GetMapping("/page")
    public ResponseResult getPostCommentsPaginated(
            @RequestParam Long postId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = commentService.getPostCommentsPaginated(postId, page, pageSize);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete")
    public ResponseResult deleteComment(@RequestParam Long commentId, @RequestParam Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseResult.success("删除评论成功", null);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取文章评论数
     */
    @GetMapping("/count")
    public ResponseResult getCommentCount(@RequestParam Long postId) {
        try {
            long count = commentService.getCommentCount(postId);
            return ResponseResult.success(count);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
} 