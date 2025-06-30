package com.example.sftploader.sftp;

import java.time.Instant;

public record SftpFileInfo(String path, String filename, long size, Instant mtime) {}
