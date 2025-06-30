package com.example.sftploader.scheduler;

import com.example.sftploader.repository.FileRecordRepository;
import com.example.sftploader.service.SftpSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SftpScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SftpScheduler.class);

    private final SftpSyncService service;
    private final FileRecordRepository repository;

    public SftpScheduler(SftpSyncService service, FileRecordRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void runDeltaScan() {
        service.deltaScan();
    }

    @javax.annotation.PostConstruct
    public void onStart() {
        if (repository.count() == 0) {
            logger.info("Running initial scan");
            service.initialScan();
        }
    }
}
