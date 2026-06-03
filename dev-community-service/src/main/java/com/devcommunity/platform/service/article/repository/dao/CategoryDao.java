package com.devcommunity.platform.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devcommunity.platform.api.model.enums.PushStatusEnum;
import com.devcommunity.platform.api.model.enums.YesOrNoEnum;
import com.devcommunity.platform.api.model.vo.PageParam;
import com.devcommunity.platform.api.model.vo.article.dto.CategoryDTO;
import com.devcommunity.platform.service.article.conveter.CategoryStructMapper;
import com.devcommunity.platform.service.article.repository.entity.CategoryDO;
import com.devcommunity.platform.service.article.repository.mapper.CategoryMapper;
import com.devcommunity.platform.service.article.repository.params.SearchCategoryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类目Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Repository
public class CategoryDao extends ServiceImpl<CategoryMapper, CategoryDO> {
    /**
     * @return
     */
    public List<CategoryDO> listAllCategoriesFromDb() {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(CategoryDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .list();
    }

    // 抽一个私有方法，构造查询条件
    private LambdaQueryChainWrapper<CategoryDO> createCategoryQuery(SearchCategoryParams params) {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNotBlank(params.getCategory()), CategoryDO::getCategoryName, params.getCategory());
    }

    /**
     * 获取所有 Categorys 列表（分页）
     *
     * @return
     */
    public List<CategoryDTO> listCategory(SearchCategoryParams params) {
        List<CategoryDO> list = createCategoryQuery(params)
                .orderByDesc(CategoryDO::getUpdateTime)
                .orderByAsc(CategoryDO::getRank)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())
                ))
                .list();
        return CategoryStructMapper.INSTANCE.toDTOs(list);
    }

    /**
     * 获取所有 Categorys 总数（分页）
     *
     * @return
     */
    public Long countCategory(SearchCategoryParams params) {
        return createCategoryQuery(params)
                .count();
    }
}
