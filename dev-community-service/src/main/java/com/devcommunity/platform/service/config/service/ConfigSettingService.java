package com.devcommunity.platform.service.config.service;

import com.devcommunity.platform.api.model.vo.PageParam;
import com.devcommunity.platform.api.model.vo.PageVo;
import com.devcommunity.platform.api.model.vo.banner.ConfigReq;
import com.devcommunity.platform.api.model.vo.banner.SearchConfigReq;
import com.devcommunity.platform.api.model.vo.banner.dto.ConfigDTO;

/**
 * Banner后台接口
 *
 * @author louzai
 * @date 2022-07-24
 */
public interface ConfigSettingService {

    /**
     * 保存
     *
     * @param configReq
     */
    void saveConfig(ConfigReq configReq);

    /**
     * 删除
     *
     * @param bannerId
     */
    void deleteConfig(Integer bannerId);

    /**
     * 操作（上线/下线）
     *
     * @param bannerId
     */
    void operateConfig(Integer bannerId, Integer pushStatus);

    /**
     * 获取 Banner 列表
     */
    PageVo<ConfigDTO> getConfigList(SearchConfigReq params);

    /**
     * 获取公告列表
     *
     * @param pageParam
     * @return
     */
    PageVo<ConfigDTO> getNoticeList(PageParam pageParam);
}
