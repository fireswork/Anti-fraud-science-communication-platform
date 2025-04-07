package com.kepu.server.service;

import com.kepu.server.entity.Comment;
import com.kepu.server.entity.Post;
import com.kepu.server.entity.User;
import com.kepu.server.repository.CommentRepository;
import com.kepu.server.repository.PostRepository;
import com.kepu.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 添加评论
     */
    public Comment addComment(Comment comment, Long postId, Long userId) {
        // 获取文章
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        comment.setPost(post);
        comment.setAuthor(user);
        
        return commentRepository.save(comment);
    }

    /**
     * 获取文章评论列表
     */
    public List<Comment> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        return commentRepository.findByPostOrderByCreateTimeDesc(post);
    }

    /**
     * 分页获取文章评论
     */
    public Map<String, Object> getPostCommentsPaginated(Long postId, Integer page, Integer pageSize) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").descending());
        Page<Comment> commentPage = commentRepository.findByPost(post, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", commentPage.getContent());
        result.put("total", commentPage.getTotalElements());
        result.put("pages", commentPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }

    /**
     * 删除评论
     */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 只有评论作者或者文章作者才能删除评论
        if (!comment.getAuthor().getId().equals(userId) && 
            !comment.getPost().getAuthor().getId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }
        
        commentRepository.delete(comment);
    }

    /**
     * 获取文章评论数
     */
    public long getCommentCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        return commentRepository.countByPost(post);
    }
} 