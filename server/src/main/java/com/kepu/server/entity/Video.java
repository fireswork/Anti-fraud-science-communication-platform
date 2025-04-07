package com.kepu.server.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String status; // published, processing, failed

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploader;

    @Column
    private Date uploadTime;

    @Column
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        uploadTime = new Date();
        updateTime = uploadTime;
        if (status == null) {
            status = "processing"; // 默认状态为处理中
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    // 构造函数
    public Video() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
} 