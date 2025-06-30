package com.example.sftploader.service;

import com.example.sftploader.entity.FileRecord;
import com.example.sftploader.repository.FileRecordRepository;
import com.example.sftploader.sftp.SftpClientWrapper;
import com.example.sftploader.sftp.SftpFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
public class SftpSyncService {
    private static final Logger logger = LoggerFactory.getLogger(SftpSyncService.class);

    private final SftpClientWrapper client;
    private final FileRecordRepository repo;

    @Value("${sftp.remote-dir}")
    private String remoteDir;

    @Value("${app.download-dir}")
    private Path downloadDir;

    public SftpSyncService(SftpClientWrapper client, FileRecordRepository repo) {
        this.client = client;
        this.repo = repo;
    }

    @Transactional
    public void initialScan() {
        logger.info("Starting initial scan");
        try {
            List<SftpFileInfo> files = client.listFiles(remoteDir);
            for (SftpFileInfo f : files) {
                repo.findByRemotePath(f.path()).ifPresentOrElse(r -> {}, () -> {
                    FileRecord fr = new FileRecord();
                    fr.setFilename(f.filename());
                    fr.setRemotePath(f.path());
                    fr.setSize(f.size());
                    fr.setRemoteMtime(f.mtime());
                    repo.save(fr);
                });
            }
        } catch (Exception e) {
            logger.error("Initial scan failed", e);
        }
    }

    @Transactional
    public void deltaScan() {
        logger.info("Starting delta scan");
        try {
            List<SftpFileInfo> files = client.listFiles(remoteDir);
            for (SftpFileInfo f : files) {
                FileRecord record = repo.findByRemotePath(f.path()).orElseGet(() -> {
                    FileRecord fr = new FileRecord();
                    fr.setFilename(f.filename());
                    fr.setRemotePath(f.path());
                    fr.setSize(f.size());
                    fr.setRemoteMtime(f.mtime());
                    return fr;
                });
                if (record.getRemoteMtime() == null || f.mtime().isAfter(record.getRemoteMtime())) {
                    Path localPath = downloadDir.resolve(f.filename());
                    client.download(f.path(), localPath);
                    record.setSize(f.size());
                    record.setRemoteMtime(f.mtime());
                    record.setDownloaded(true);
                    record.setLastDownloaded(Instant.now());
                    repo.save(record);
                    logger.info("Downloaded {}", f.filename());
                }
            }
        } catch (Exception e) {
            logger.error("Delta scan failed", e);
        }
    }
}
