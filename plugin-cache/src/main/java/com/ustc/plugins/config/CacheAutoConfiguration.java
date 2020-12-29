package com.ustc.plugins.config;

import com.ustc.plugins.CacheAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * company: guochuang software co.ltd<br>
 * date: 2020/2/19<br>
 * filename: CacheAutoConfiguration<br>
 * <p>
 * description:<br>
 * 请求缓存配置类
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
@Configuration
@ConditionalOnWebApplication
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "cacheAspect")
    public CacheAspect cacheAspect() {
        return new CacheAspect();
    }

}
