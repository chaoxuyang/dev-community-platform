package com.devcommunity.platform.service.sync.canal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "canal.sync")
public class CanalSyncProperties {
    private boolean enabled = false;
    private String host = "127.0.0.1";
    private int port = 11111;
    private String destination = "example";
    private String username = "";
    private String password = "";
    private String filter = ".*\\.article";
    private int batchSize = 100;
    private long emptySleepMillis = 1000L;
}
