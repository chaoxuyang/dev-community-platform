package com.devcommunity.platform.service.article.service;

import com.devcommunity.platform.api.model.vo.PageListVo;
import com.devcommunity.platform.api.model.vo.PageParam;
import com.devcommunity.platform.api.model.vo.article.dto.ArticleDTO;

/**
 * @author YiHui
 * @date 2022/9/26
 */
public interface ArticleRecommendService {
    /**
     * 文章关联推荐
     *
     * @param article
     * @param pageParam
     * @return
     */
    PageListVo<ArticleDTO> relatedRecommend(Long article, PageParam pageParam);
}
