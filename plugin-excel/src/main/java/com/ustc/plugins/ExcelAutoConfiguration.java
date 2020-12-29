package com.ustc.plugins;

import com.ustc.plugins.service.ExcelService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * company: guochuang software co.ltd<br>
 * date: 2020/8/12<br>
 * filename: ExcelConfig<br>
 * <p>
 * description:<br>
 * excel工具配置
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
@Configuration
@ConditionalOnWebApplication
public class ExcelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "excelService")
    public ExcelService excelService(ApplicationContext applicationContext){
        ExcelService excelService = new ExcelService();
        excelService.setApplicationContext(applicationContext);
        return excelService;
    }
}
