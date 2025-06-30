package com.example.sftploader.controller;

import com.example.sftploader.service.SftpSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sftp")
public class SftpController {
    private final SftpSyncService service;

    public SftpController(SftpSyncService service) {
        this.service = service;
    }

    @PostMapping("/delta-scan")
    public ResponseEntity<Void> triggerDeltaScan() {
        service.deltaScan();
        return ResponseEntity.accepted().build();
    }
}
