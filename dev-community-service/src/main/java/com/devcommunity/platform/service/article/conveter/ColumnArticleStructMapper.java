package com.devcommunity.platform.service.article.conveter;

import com.devcommunity.platform.api.model.vo.article.ColumnArticleReq;
import com.devcommunity.platform.api.model.vo.article.SearchColumnArticleReq;
import com.devcommunity.platform.service.article.repository.entity.ColumnArticleDO;
import com.devcommunity.platform.service.article.repository.params.ColumnArticleParams;
import com.devcommunity.platform.service.article.repository.params.SearchColumnArticleParams;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ColumnArticleStructMapper {
    ColumnArticleStructMapper INSTANCE = Mappers.getMapper( ColumnArticleStructMapper.class );

    SearchColumnArticleParams toSearchParams(SearchColumnArticleReq req);

    ColumnArticleParams toParams(ColumnArticleReq req);

    ColumnArticleDO reqToDO(ColumnArticleReq req);
}
