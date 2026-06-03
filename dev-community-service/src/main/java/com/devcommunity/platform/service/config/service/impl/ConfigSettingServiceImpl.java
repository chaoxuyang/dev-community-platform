package com.devcommunity.platform.service.config.service.impl;

import com.devcommunity.platform.api.model.enums.YesOrNoEnum;
import com.devcommunity.platform.api.model.vo.PageParam;
import com.devcommunity.platform.api.model.vo.PageVo;
import com.devcommunity.platform.api.model.vo.banner.ConfigReq;
import com.devcommunity.platform.api.model.vo.banner.SearchConfigReq;
import com.devcommunity.platform.api.model.vo.banner.dto.ConfigDTO;
import com.devcommunity.platform.core.util.NumUtil;
import com.devcommunity.platform.service.config.converter.ConfigStructMapper;
import com.devcommunity.platform.service.config.repository.dao.ConfigDao;
import com.devcommunity.platform.service.config.repository.entity.ConfigDO;
import com.devcommunity.platform.service.config.repository.params.SearchConfigParams;
import com.devcommunity.platform.service.config.service.ConfigSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner后台接口
 *
 * @author louzai
 * @date 2022-07-24
 */
@Service
public class ConfigSettingServiceImpl implements ConfigSettingService {

    @Autowired
    private ConfigDao configDao;

    @Override
    public void saveConfig(ConfigReq configReq) {
        ConfigDO configDO = ConfigStructMapper.INSTANCE.toDO(configReq);
        if (NumUtil.nullOrZero(configReq.getConfigId())) {
            configDao.save(configDO);
        } else {
            configDO.setId(configReq.getConfigId());
            configDao.updateById(configDO);
        }
    }

    @Override
    public void deleteConfig(Integer configId) {
        ConfigDO configDO = configDao.getById(configId);
        if (configDO != null){
            configDO.setDeleted(YesOrNoEnum.YES.getCode());
            configDao.updateById(configDO);
        }
    }

    @Override
    public void operateConfig(Integer configId, Integer pushStatus) {
        ConfigDO configDO = configDao.getById(configId);
        if (configDO != null){
            configDO.setStatus(pushStatus);
            configDao.updateById(configDO);
        }
    }

    @Override
    public PageVo<ConfigDTO> getConfigList(SearchConfigReq req) {
        // 转换
        SearchConfigParams params = ConfigStructMapper.INSTANCE.toSearchParams(req);
        // 查询
        List<ConfigDTO> configDTOS = configDao.listBanner(params);
        Long totalCount = configDao.countConfig(params);
        return PageVo.build(configDTOS, params.getPageSize(), params.getPageNum(), totalCount);
    }

    @Override
    public PageVo<ConfigDTO> getNoticeList(PageParam pageParam) {
        List<ConfigDTO> configDTOS = configDao.listNotice(pageParam);
        Integer totalCount = configDao.countNotice();
        return PageVo.build(configDTOS, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }
}
