package com.devcommunity.platform.service.sync.canal;

import com.devcommunity.platform.service.constant.EsIndexConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CanalArticleIndexService {
    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    public void indexArticle(Map<String, String> columns) {
        String id = columns.get("id");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        if (restHighLevelClient == null) {
            log.warn("Skip article index sync because RestHighLevelClient is not configured, articleId={}", id);
            return;
        }

        Map<String, Object> document = new HashMap<>();
        putIfPresent(document, "id", columns.get("id"));
        putIfPresent(document, "title", columns.get("title"));
        putIfPresent(document, "short_title", columns.get("short_title"));
        putIfPresent(document, "url_slug", columns.get("url_slug"));
        putIfPresent(document, "summary", columns.get("summary"));
        putIfPresent(document, "category_id", columns.get("category_id"));
        putIfPresent(document, "status", columns.get("status"));
        putIfPresent(document, "deleted", columns.get("deleted"));
        putIfPresent(document, "update_time", columns.get("update_time"));

        IndexRequest request = new IndexRequest(EsIndexConstant.ES_INDEX_ARTICLE)
                .id(id)
                .source(document, XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Failed to sync article to Elasticsearch, articleId={}", id, e);
        }
    }

    public void deleteArticle(Long articleId) {
        if (articleId == null) {
            return;
        }
        if (restHighLevelClient == null) {
            log.warn("Skip article index delete because RestHighLevelClient is not configured, articleId={}", articleId);
            return;
        }

        DeleteRequest request = new DeleteRequest(EsIndexConstant.ES_INDEX_ARTICLE, "_doc", String.valueOf(articleId));
        try {
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Failed to delete article from Elasticsearch, articleId={}", articleId, e);
        }
    }

    private void putIfPresent(Map<String, Object> document, String field, String value) {
        if (value != null) {
            document.put(field, value);
        }
    }
}
