package com.example.sftploader.repository;

import com.example.sftploader.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    Optional<FileRecord> findByRemotePath(String remotePath);
    List<FileRecord> findAllByRemoteMtimeAfter(Instant timestamp);
}
