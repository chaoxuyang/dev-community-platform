package com.devcommunity.platform.service.sync.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "canal.sync", name = "enabled", havingValue = "true")
public class CanalArticleSyncWorker {
    private final CanalSyncProperties properties;
    private final CanalArticleEntryHandler articleEntryHandler;
    private volatile boolean running;
    private Thread workerThread;
    private CanalConnector connector;

    public CanalArticleSyncWorker(CanalSyncProperties properties, CanalArticleEntryHandler articleEntryHandler) {
        this.properties = properties;
        this.articleEntryHandler = articleEntryHandler;
    }

    @PostConstruct
    public void start() {
        running = true;
        workerThread = new Thread(this::syncLoop, "canal-article-sync-worker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (connector != null) {
            connector.disconnect();
        }
    }

    private void syncLoop() {
        connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(properties.getHost(), properties.getPort()),
                properties.getDestination(),
                properties.getUsername(),
                properties.getPassword());
        try {
            connector.connect();
            connector.subscribe(properties.getFilter());
            connector.rollback();
            while (running) {
                Message message = connector.getWithoutAck(properties.getBatchSize());
                long batchId = message.getId();
                List<CanalEntry.Entry> entries = message.getEntries();
                if (batchId == -1 || entries.isEmpty()) {
                    sleepQuietly();
                    continue;
                }
                try {
                    for (CanalEntry.Entry entry : entries) {
                        articleEntryHandler.handleEntry(entry);
                    }
                    connector.ack(batchId);
                } catch (Exception e) {
                    connector.rollback(batchId);
                    log.error("Failed to handle Canal batch, batchId={}", batchId, e);
                }
            }
        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(properties.getEmptySleepMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
