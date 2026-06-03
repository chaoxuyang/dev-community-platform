package com.devcommunity.platform.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author YiHui
 * @date 2022/7/6
 */
@Configuration
@ComponentScan("com.devcommunity.platform.service")
@MapperScan(basePackages = {
        "com.devcommunity.platform.service.article.repository.mapper",
        "com.devcommunity.platform.service.user.repository.mapper",
        "com.devcommunity.platform.service.comment.repository.mapper",
        "com.devcommunity.platform.service.config.repository.mapper",
        "com.devcommunity.platform.service.statistics.repository.mapper",
        "com.devcommunity.platform.service.notify.repository.mapper",
        "com.devcommunity.platform.service.shortlink.repository.mapper",
})
public class ServiceAutoConfig {


}
