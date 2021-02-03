package com.ustc.plugins.annotation;

import com.ustc.plugins.model.DefaultTitleInfo;

import java.lang.annotation.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/3/13<br>
 * filename: Title<br>
 * <p>
 * description:<br>
 * excel生成时的标题描述注解
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {

    //读取标题的类型
    TitleEnum type() default TitleEnum.title;

    //标题的行数
    int titleRow() default 0;

    //标题信息描述类
    Class titleInfo() default DefaultTitleInfo.class;
}
