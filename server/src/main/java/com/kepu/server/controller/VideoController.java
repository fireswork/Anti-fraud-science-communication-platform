package com.kepu.server.controller;

import com.kepu.server.dto.ResponseResult;
import com.kepu.server.entity.Video;
import com.kepu.server.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

@RestController
@RequestMapping("/video")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    // 视频存储的绝对路径 - 使用已经存在的目录
    private static final String VIDEO_DIR = System.getProperty("user.dir") + "/uploads/videos";

    @Autowired
    private VideoService videoService;

    /**
     * 上传视频
     */
    @PostMapping("/upload")
    public ResponseResult uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId) {
        try {
            // 确保视频目录存在
            File videoDir = new File(VIDEO_DIR);
            if (!videoDir.exists()) {
                videoDir.mkdirs();
                System.out.println("创建视频目录: " + videoDir.getAbsolutePath());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File videoFile = new File(videoDir, filename);
            System.out.println("保存视频到: " + videoFile.getAbsolutePath());
            file.transferTo(videoFile);
            
            // 视频访问URL
            String videoUrl = "/video/play/" + filename;
            
            // 创建视频记录
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setUrl(videoUrl);
            
            Video savedVideo = videoService.createVideo(video, userId);
            
            return ResponseResult.success("视频上传成功", savedVideo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail(500, "视频上传失败: " + e.getMessage());
        }
    }

    /**
     * 播放视频
     */
    @GetMapping("/play/{filename:.+}")
    public void playVideo(@PathVariable String filename, HttpServletResponse response) {
        System.out.println("开始处理视频播放请求：" + filename);
        
        // 先检查第一个位置
        File file = new File(VIDEO_DIR, filename);
        if (!file.exists()) {
            // 再检查第二个位置
            file = new File(System.getProperty("user.dir") + "/server/uploads/videos/", filename);
            if (!file.exists()) {
                // 最后检查当前目录
                file = new File(filename);
            }
        }
        
        System.out.println("查找视频文件：" + file.getAbsolutePath());
        System.out.println("文件是否存在：" + file.exists());
        
        if (!file.exists()) {
            System.out.println("视频文件不存在：" + filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            // 确定文件类型
            String contentType;
            if (filename.toLowerCase().endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (filename.toLowerCase().endsWith(".webm")) {
                contentType = "video/webm";
            } else if (filename.toLowerCase().endsWith(".ogg")) {
                contentType = "video/ogg";
            } else {
                contentType = "application/octet-stream";
            }
            
            System.out.println("Content-Type: " + contentType);
            
            // 设置响应头
            response.reset();
            response.setContentType(contentType);
            response.setContentLength((int) file.length());
            response.setHeader("Access-Control-Allow-Origin", "*");
            
            // 直接使用简单的输入输出流
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
                
                byte[] buffer = new byte[1024 * 16];  // 16KB缓冲区
                int bytesRead;
                
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                
                bos.flush();
                System.out.println("视频发送完成：" + filename);
            }
        } catch (IOException e) {
            System.err.println("处理视频流异常：" + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取视频列表
     */
    @GetMapping("/list")
    public ResponseResult getVideoList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId) {
        try {
            Map<String, Object> result = videoService.getVideoList(page, pageSize, title, status, uploaderId);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/detail")
    public ResponseResult getVideoDetail(@RequestParam Long videoId) {
        try {
            Video video = videoService.getVideoById(videoId);
            return ResponseResult.success(video);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 更新视频信息
     */
    @PutMapping("/update")
    public ResponseResult updateVideo(@RequestBody Map<String, Object> params) {
        try {
            Long videoId = Long.parseLong(params.get("videoId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            
            Video video = new Video();
            video.setId(videoId);
            
            if (params.containsKey("title")) {
                video.setTitle(params.get("title").toString());
            }
            if (params.containsKey("description")) {
                video.setDescription(params.get("description").toString());
            }
            if (params.containsKey("status")) {
                video.setStatus(params.get("status").toString());
            }
            
            Video updatedVideo = videoService.updateVideo(video, userId);
            return ResponseResult.success("视频信息更新成功", updatedVideo);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/delete")
    public ResponseResult deleteVideo(@RequestParam Long videoId, @RequestParam Long userId) {
        try {
            // 获取视频信息
            Video video = videoService.getVideoById(videoId);
            if (video != null && video.getUrl() != null) {
                // 删除视频文件
                String filename = video.getUrl().substring(video.getUrl().lastIndexOf('/') + 1);
                File videoFile = new File(VIDEO_DIR, filename);
                if (videoFile.exists()) {
                    videoFile.delete();
                    System.out.println("删除视频文件: " + videoFile.getAbsolutePath());
                }
            }
            
            videoService.deleteVideo(videoId, userId);
            return ResponseResult.success("视频删除成功", null);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    /**
     * 检查视频是否存在
     */
    @GetMapping("/check/{filename:.+}")
    public ResponseResult checkVideoFile(@PathVariable String filename) {
        try {
            System.out.println("========== 检查视频文件请求 ==========");
            System.out.println("请求的文件名: " + filename);
            System.out.println("当前系统路径: " + System.getProperty("user.dir"));
            System.out.println("配置的视频目录: " + VIDEO_DIR);
            
            File videoFile = new File(VIDEO_DIR, filename);
            System.out.println("查找文件路径: " + videoFile.getAbsolutePath());
            
            boolean exists = videoFile.exists() && videoFile.isFile() && videoFile.canRead();
            System.out.println("文件存在状态: " + exists);
            
            if (!exists) {
                // 尝试在其他可能的位置查找
                File altFile = new File(System.getProperty("user.dir") + "/server/uploads/videos/", filename);
                System.out.println("尝试备用路径1: " + altFile.getAbsolutePath());
                boolean altExists = altFile.exists() && altFile.isFile() && altFile.canRead();
                System.out.println("备用路径1文件存在: " + altExists);
                
                if (altExists) {
                    videoFile = altFile;
                    exists = true;
                    System.out.println("使用备用路径1的文件");
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("exists", exists);
            data.put("path", videoFile.getAbsolutePath());
            if (exists) {
                data.put("size", videoFile.length());
                data.put("readable", videoFile.canRead());
                data.put("lastModified", new java.util.Date(videoFile.lastModified()));
            }
            
            System.out.println("检查结果: " + (exists ? "文件存在" : "文件不存在"));
            System.out.println("========== 检查视频文件请求结束 ==========");
            
            return ResponseResult.success(data);
        } catch (Exception e) {
            System.out.println("检查视频文件出错: " + e.getMessage());
            e.printStackTrace();
            return ResponseResult.fail(500, "检查文件失败: " + e.getMessage());
        }
    }
} 