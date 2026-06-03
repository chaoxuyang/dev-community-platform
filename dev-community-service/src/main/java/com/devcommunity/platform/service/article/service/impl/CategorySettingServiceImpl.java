package com.devcommunity.platform.service.article.service.impl;

import com.devcommunity.platform.api.model.vo.PageVo;
import com.devcommunity.platform.api.model.vo.article.CategoryReq;
import com.devcommunity.platform.api.model.vo.article.SearchCategoryReq;
import com.devcommunity.platform.api.model.vo.article.dto.CategoryDTO;
import com.devcommunity.platform.core.util.NumUtil;
import com.devcommunity.platform.service.article.conveter.ArticleConverter;
import com.devcommunity.platform.service.article.conveter.CategoryStructMapper;
import com.devcommunity.platform.service.article.repository.dao.CategoryDao;
import com.devcommunity.platform.service.article.repository.entity.CategoryDO;
import com.devcommunity.platform.service.article.repository.params.SearchCategoryParams;
import com.devcommunity.platform.service.article.service.CategoryService;
import com.devcommunity.platform.service.article.service.CategorySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
@Service
public class CategorySettingServiceImpl implements CategorySettingService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void saveCategory(CategoryReq categoryReq) {
        CategoryDO categoryDO = CategoryStructMapper.INSTANCE.toDO(categoryReq);
        if (NumUtil.nullOrZero(categoryReq.getCategoryId())) {
            categoryDao.save(categoryDO);
        } else {
            categoryDO.setId(categoryReq.getCategoryId());
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null){
            categoryDao.removeById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void operateCategory(Integer categoryId, Integer pushStatus) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null){
            categoryDO.setStatus(pushStatus);
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public PageVo<CategoryDTO> getCategoryList(SearchCategoryReq req) {
        // 转换
        SearchCategoryParams params = CategoryStructMapper.INSTANCE.toSearchParams(req);
        // 查询
        List<CategoryDTO> categoryDTOS = categoryDao.listCategory(params);
        Long totalCount = categoryDao.countCategory(params);
        return PageVo.build(categoryDTOS, params.getPageSize(), params.getPageNum(), totalCount);
    }
}
