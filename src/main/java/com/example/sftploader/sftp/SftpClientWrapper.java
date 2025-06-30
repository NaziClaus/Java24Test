package com.example.sftploader.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class SftpClientWrapper {

    private static final Logger logger = LoggerFactory.getLogger(SftpClientWrapper.class);

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port:22}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public List<SftpFileInfo> listFiles(String path) throws Exception {
        ChannelSftp channelSftp = null;
        Session session = null;
        try {
            session = createSession();
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            List<SftpFileInfo> files = new ArrayList<>();
            @SuppressWarnings("unchecked")
            var vector = channelSftp.ls(path);
            for (var entry : vector) {
                ChannelSftp.LsEntry ls = (ChannelSftp.LsEntry) entry;
                if (!ls.getAttrs().isDir()) {
                    files.add(new SftpFileInfo(path + "/" + ls.getFilename(), ls.getFilename(),
                            ls.getAttrs().getSize(), Instant.ofEpochSecond(ls.getAttrs().getMTime())));
                }
            }
            return files;
        } finally {
            if (channelSftp != null) channelSftp.disconnect();
            if (session != null) session.disconnect();
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void download(String remote, Path local) throws Exception {
        ChannelSftp channelSftp = null;
        Session session = null;
        try {
            session = createSession();
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            Path tempFile = local.resolveSibling(local.getFileName() + ".part");
            try (InputStream in = channelSftp.get(remote)) {
                Files.copy(in, tempFile);
            }
            Files.move(tempFile, local);
        } catch (IOException e) {
            throw e;
        } finally {
            if (channelSftp != null) channelSftp.disconnect();
            if (session != null) session.disconnect();
        }
    }

    @Recover
    public void recover(Exception e, String remote, Path local) {
        logger.error("Failed to process SFTP operation for {}", remote, e);
    }

    private Session createSession() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }
}
