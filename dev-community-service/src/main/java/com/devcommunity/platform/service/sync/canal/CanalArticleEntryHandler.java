package com.devcommunity.platform.service.sync.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CanalArticleEntryHandler {
    private static final String ARTICLE_TABLE = "article";
    private static final String ONLINE_STATUS = "1";
    private static final String NOT_DELETED = "0";

    private final CanalArticleIndexService canalArticleIndexService;

    public CanalArticleEntryHandler(CanalArticleIndexService canalArticleIndexService) {
        this.canalArticleIndexService = canalArticleIndexService;
    }

    public SyncResult handleEntry(CanalEntry.Entry entry) throws InvalidProtocolBufferException {
        SyncResult result = new SyncResult();
        if (entry == null || entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
            return result;
        }
        String tableName = entry.getHeader().getTableName();
        if (!ARTICLE_TABLE.equalsIgnoreCase(tableName)) {
            return result;
        }

        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            if (rowChange.getEventType() == CanalEntry.EventType.DELETE) {
                Long articleId = parseId(toColumnMap(rowData.getBeforeColumnsList()));
                canalArticleIndexService.deleteArticle(articleId);
                result.deleted++;
                continue;
            }

            Map<String, String> columns = toColumnMap(rowData.getAfterColumnsList());
            Long articleId = parseId(columns);
            if (shouldIndex(columns)) {
                canalArticleIndexService.indexArticle(columns);
                result.indexed++;
            } else {
                canalArticleIndexService.deleteArticle(articleId);
                result.deleted++;
            }
        }
        return result;
    }

    private Map<String, String> toColumnMap(List<CanalEntry.Column> columns) {
        Map<String, String> columnMap = new HashMap<>();
        for (CanalEntry.Column column : columns) {
            columnMap.put(column.getName(), column.getValue());
        }
        return columnMap;
    }

    private boolean shouldIndex(Map<String, String> columns) {
        return ONLINE_STATUS.equals(columns.get("status")) && NOT_DELETED.equals(columns.get("deleted"));
    }

    private Long parseId(Map<String, String> columns) {
        String id = columns.get("id");
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return Long.valueOf(id);
    }

    @Getter
    public static class SyncResult {
        private int indexed;
        private int deleted;
    }
}
