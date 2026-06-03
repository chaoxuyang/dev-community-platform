package com.devcommunity.platform.service.article.service;

import com.devcommunity.platform.api.model.vo.PageVo;
import com.devcommunity.platform.api.model.vo.article.CategoryReq;
import com.devcommunity.platform.api.model.vo.article.SearchCategoryReq;
import com.devcommunity.platform.api.model.vo.article.dto.CategoryDTO;

/**
 * 分类后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
public interface CategorySettingService {

    void saveCategory(CategoryReq categoryReq);

    void deleteCategory(Integer categoryId);

    void operateCategory(Integer categoryId, Integer pushStatus);

    /**
     * 获取category列表
     *
     * @param pageParam
     * @return
     */
    PageVo<CategoryDTO> getCategoryList(SearchCategoryReq params);
}
