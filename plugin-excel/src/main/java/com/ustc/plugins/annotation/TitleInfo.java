package com.ustc.plugins.annotation;

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
@Target(ElementType.FIELD)
public @interface TitleInfo {
    //标题
    String value();

    //行坐标
    int rowIndex() default 0;

    //列坐标
    int colIndex() default -1;

    //行宽
    int colspan() default -1;

    //列宽
    int rowspan() default -1;
}
