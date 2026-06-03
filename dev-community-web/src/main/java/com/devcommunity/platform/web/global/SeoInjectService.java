package com.devcommunity.platform.web.global;

import com.devcommunity.platform.api.model.context.ReqInfoContext;
import com.devcommunity.platform.api.model.vo.article.dto.ArticleDTO;
import com.devcommunity.platform.api.model.vo.article.dto.ColumnArticlesDTO;
import com.devcommunity.platform.api.model.vo.article.dto.ColumnDTO;
import com.devcommunity.platform.api.model.vo.article.dto.TagDTO;
import com.devcommunity.platform.api.model.vo.seo.Seo;
import com.devcommunity.platform.api.model.vo.seo.SeoTagVo;
import com.devcommunity.platform.core.util.DateUtil;
import com.devcommunity.platform.web.config.GlobalViewConfig;
import com.devcommunity.platform.web.front.article.vo.ArticleDetailVo;
import com.devcommunity.platform.web.front.user.vo.UserHomeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 页面 SEO 元信息注入服务。
 */
@Service
public class SeoInjectService {
    private static final String SITE_NAME = "开发者技术社区平台";
    private static final String SITE_TITLE = "开发者技术社区平台 - 技术内容创作与交流社区";
    private static final String KEYWORDS = "开发者社区,技术社区,Java,Spring Boot,MySQL,Redis,RabbitMQ,Elasticsearch,MyBatis-Plus,全文搜索,缓存,消息通知";
    private static final String DESCRIPTION = "开发者技术社区平台面向技术内容创作与交流场景，支持文章发布、评论点赞、消息通知、搜索检索、登录认证和后台管理等核心功能。系统采用 Spring Boot、MyBatis-Plus、MySQL、Redis、Caffeine、RabbitMQ、Elasticsearch 等技术栈实现。";

    @Resource
    private GlobalViewConfig globalViewConfig;

    public void initColumnSeo(ArticleDetailVo detail) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        ArticleDTO article = detail.getArticle();
        String title = article.getTitle();
        String description = article.getSummary();
        String authorName = detail.getAuthor().getUserName();
        String updateTime = DateUtil.time2LocalTime(article.getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(article.getCreateTime()).toString();
        String image = article.getCover();

        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));
        list.add(new SeoTagVo("og:updated_time", updateTime));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", article.getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));
        list.add(new SeoTagVo("article:section", article.getCategory().getCategory()));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", description));
        list.add(new SeoTagVo("keywords", article.getCategory().getCategory() + "," + article.getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));

        if (StringUtils.isNotBlank(image)) {
            list.add(new SeoTagVo("og:image", image));
            jsonLd.put("image", image);
        }

        fillArticleJsonLd(jsonLd, title, description, authorName, updateTime, publishedTime);
        ReqInfoContext.getReqInfo().setSeo(seo);
    }

    public void initColumnSeo(ColumnArticlesDTO detail, ColumnDTO column) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        ArticleDTO article = detail.getArticle();
        String title = article.getTitle();
        String description = article.getSummary();
        String authorName = column.getAuthorName();
        String updateTime = DateUtil.time2LocalTime(article.getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(article.getCreateTime()).toString();
        String image = column.getCover();

        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));
        list.add(new SeoTagVo("og:updated_time", updateTime));
        list.add(new SeoTagVo("og:image", image));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", article.getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));
        list.add(new SeoTagVo("article:section", column.getColumn()));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", description));
        list.add(new SeoTagVo("keywords", article.getCategory().getCategory() + "," + article.getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));

        fillArticleJsonLd(jsonLd, title, description, authorName, updateTime, publishedTime);
        jsonLd.put("image", image);

        Map<String, Object> isPartOf = new HashMap<>();
        isPartOf.put("@type", "Course");
        isPartOf.put("name", column.getColumn());
        isPartOf.put("description", column.getIntroduction());
        jsonLd.put("isPartOf", isPartOf);

        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
    }

    public void initUserSeo(UserHomeVo user) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String userName = user.getUserHome().getUserName();
        String title = SITE_NAME + " | " + userName + " 的主页";
        String profile = user.getUserHome().getProfile();
        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", profile));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("article:tag", "后端,前端,Java,Spring Boot,架构设计"));
        list.add(new SeoTagVo("article:section", "主页"));
        list.add(new SeoTagVo("article:author", userName));

        list.add(new SeoTagVo("author", userName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", profile));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        jsonLd.put("headline", title);
        jsonLd.put("description", profile);
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", userName);
        jsonLd.put("author", author);

        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
    }

    public Seo defaultSeo() {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        list.add(new SeoTagVo("og:title", SITE_TITLE));
        list.add(new SeoTagVo("og:description", DESCRIPTION));
        list.add(new SeoTagVo("og:type", "website"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("title", SITE_TITLE));
        list.add(new SeoTagVo("description", DESCRIPTION));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        Map<String, Object> jsonLd = seo.getJsonLd();
        jsonLd.put("@context", "https://schema.org");
        jsonLd.put("@type", "WebSite");
        jsonLd.put("name", SITE_NAME);
        jsonLd.put("url", globalViewConfig.getHost());
        jsonLd.put("description", DESCRIPTION);

        Map<String, Object> potentialAction = new HashMap<>();
        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", globalViewConfig.getHost() + "/search?q={search_term_string}");
        potentialAction.put("query-input", "required name=search_term_string");
        jsonLd.put("potentialAction", potentialAction);

        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
        return seo;
    }

    private void fillArticleJsonLd(Map<String, Object> jsonLd, String title, String description,
                                   String authorName, String updateTime, String publishedTime) {
        jsonLd.put("@type", "TechArticle");
        jsonLd.put("headline", title);
        jsonLd.put("description", description);

        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", authorName);
        jsonLd.put("author", author);

        jsonLd.put("dateModified", updateTime);
        jsonLd.put("datePublished", publishedTime);

        Map<String, Object> publisher = new HashMap<>();
        publisher.put("@type", "Organization");
        publisher.put("name", SITE_NAME);

        Map<String, Object> logo = new HashMap<>();
        logo.put("@type", "ImageObject");
        logo.put("url", globalViewConfig.getHost() + "/img/logo.svg");
        publisher.put("logo", logo);

        jsonLd.put("publisher", publisher);
    }

    private Seo initBasicSeoTag() {
        List<SeoTagVo> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("@context", "https://schema.org");

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = globalViewConfig.getHost() + request.getRequestURI();

        list.add(new SeoTagVo("og:url", url));
        map.put("url", url);

        return Seo.builder().jsonLd(map).ogp(list).build();
    }
}
