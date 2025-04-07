package com.kepu.server.service;

import com.kepu.server.entity.Post;
import com.kepu.server.entity.User;
import com.kepu.server.repository.PostRepository;
import com.kepu.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建文章
     */
    public Post createPost(Post post, Long userId) {
        // 获取作者信息
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        post.setAuthor(author);
        return postRepository.save(post);
    }

    /**
     * 获取文章详情
     */
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
    }

    /**
     * 更新文章
     */
    public Post updatePost(Post post, Long userId) {
        Post existingPost = getPostById(post.getId());
        
        // 检查是否是作者本人
        if (!existingPost.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("只有作者本人可以修改文章");
        }
        
        // 更新文章信息
        if (StringUtils.hasText(post.getTitle())) {
            existingPost.setTitle(post.getTitle());
        }
        if (StringUtils.hasText(post.getContent())) {
            existingPost.setContent(post.getContent());
        }
        if (StringUtils.hasText(post.getLocation())) {
            existingPost.setLocation(post.getLocation());
        }
        if (post.getLongitude() != null) {
            existingPost.setLongitude(post.getLongitude());
        }
        if (post.getLatitude() != null) {
            existingPost.setLatitude(post.getLatitude());
        }
        
        return postRepository.save(existingPost);
    }

    /**
     * 删除文章
     */
    public void deletePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        
        // 检查是否是作者本人
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("只有作者本人可以删除文章");
        }
        
        postRepository.delete(post);
    }

    /**
     * 获取文章列表（分页）
     */
    public Map<String, Object> getPostList(Integer page, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").descending());
        Page<Post> postPage;
        
        if (StringUtils.hasText(keyword)) {
            postPage = postRepository.findByTitleContainingOrContentContaining(
                    keyword, keyword, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", postPage.getContent());
        result.put("total", postPage.getTotalElements());
        result.put("pages", postPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }

    /**
     * 获取用户的文章列表
     */
    public Map<String, Object> getUserPosts(Long userId, Integer page, Integer pageSize) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").descending());
        Page<Post> postPage = postRepository.findByAuthor(author, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", postPage.getContent());
        result.put("total", postPage.getTotalElements());
        result.put("pages", postPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }

    /**
     * 按位置搜索文章
     */
    public Map<String, Object> searchPostsByLocation(String location, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").descending());
        Page<Post> postPage = postRepository.findByLocationContaining(location, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", postPage.getContent());
        result.put("total", postPage.getTotalElements());
        result.put("pages", postPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }
} 