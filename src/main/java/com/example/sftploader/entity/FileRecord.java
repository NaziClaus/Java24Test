package com.example.sftploader.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "file_records")
public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "remote_path", unique = true, nullable = false)
    private String remotePath;

    private Long size;

    @Column(name = "remote_mtime")
    private Instant remoteMtime;

    private Boolean downloaded = Boolean.FALSE;

    @Column(name = "last_downloaded")
    private Instant lastDownloaded;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Instant getRemoteMtime() {
        return remoteMtime;
    }

    public void setRemoteMtime(Instant remoteMtime) {
        this.remoteMtime = remoteMtime;
    }

    public Boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        this.downloaded = downloaded;
    }

    public Instant getLastDownloaded() {
        return lastDownloaded;
    }

    public void setLastDownloaded(Instant lastDownloaded) {
        this.lastDownloaded = lastDownloaded;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
