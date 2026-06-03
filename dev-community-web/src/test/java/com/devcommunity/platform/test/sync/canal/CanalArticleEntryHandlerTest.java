package com.devcommunity.platform.test.sync.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.devcommunity.platform.service.sync.canal.CanalArticleEntryHandler;
import com.devcommunity.platform.service.sync.canal.CanalArticleIndexService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CanalArticleEntryHandlerTest {

    @Test
    public void shouldIndexArticleWhenCanalReceivesInsertRow() throws Exception {
        RecordingIndexService indexService = new RecordingIndexService();
        CanalArticleEntryHandler handler = new CanalArticleEntryHandler(indexService);

        CanalArticleEntryHandler.SyncResult result = handler.handleEntry(entry(
                CanalEntry.EventType.INSERT,
                column("id", "1001"),
                column("title", "Redis cache consistency"),
                column("short_title", "Redis consistency"),
                column("summary", "Update MySQL first and then delete Redis"),
                column("category_id", "10"),
                column("status", "1"),
                column("deleted", "0")
        ));

        Assertions.assertEquals(1, result.getIndexed());
        Assertions.assertEquals("1001", indexService.indexed.get("id"));
        Assertions.assertEquals("Redis cache consistency", indexService.indexed.get("title"));
        Assertions.assertNull(indexService.deletedId);
    }

    @Test
    public void shouldDeleteArticleIndexWhenCanalReceivesDeleteRow() throws Exception {
        RecordingIndexService indexService = new RecordingIndexService();
        CanalArticleEntryHandler handler = new CanalArticleEntryHandler(indexService);

        CanalArticleEntryHandler.SyncResult result = handler.handleEntry(deleteEntry("1002"));

        Assertions.assertEquals(1, result.getDeleted());
        Assertions.assertEquals(Long.valueOf(1002L), indexService.deletedId);
    }

    private CanalEntry.Entry entry(CanalEntry.EventType eventType, CanalEntry.Column... columns) {
        CanalEntry.RowData rowData = CanalEntry.RowData.newBuilder()
                .addAllAfterColumns(java.util.Arrays.asList(columns))
                .build();
        CanalEntry.RowChange rowChange = CanalEntry.RowChange.newBuilder()
                .setEventType(eventType)
                .addRowDatas(rowData)
                .build();
        return CanalEntry.Entry.newBuilder()
                .setEntryType(CanalEntry.EntryType.ROWDATA)
                .setHeader(CanalEntry.Header.newBuilder().setTableName("article").build())
                .setStoreValue(rowChange.toByteString())
                .build();
    }

    private CanalEntry.Entry deleteEntry(String id) {
        CanalEntry.RowData rowData = CanalEntry.RowData.newBuilder()
                .addBeforeColumns(column("id", id))
                .build();
        CanalEntry.RowChange rowChange = CanalEntry.RowChange.newBuilder()
                .setEventType(CanalEntry.EventType.DELETE)
                .addRowDatas(rowData)
                .build();
        return CanalEntry.Entry.newBuilder()
                .setEntryType(CanalEntry.EntryType.ROWDATA)
                .setHeader(CanalEntry.Header.newBuilder().setTableName("article").build())
                .setStoreValue(rowChange.toByteString())
                .build();
    }

    private CanalEntry.Column column(String name, String value) {
        return CanalEntry.Column.newBuilder().setName(name).setValue(value).build();
    }

    private static class RecordingIndexService extends CanalArticleIndexService {
        private Map<String, String> indexed;
        private Long deletedId;

        @Override
        public void indexArticle(Map<String, String> columns) {
            this.indexed = columns;
        }

        @Override
        public void deleteArticle(Long articleId) {
            this.deletedId = articleId;
        }
    }
}
