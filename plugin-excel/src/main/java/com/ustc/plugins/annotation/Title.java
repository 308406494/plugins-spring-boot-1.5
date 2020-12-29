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
public @interface Title {
    //标题
    String value();

    //是否开启正则表达式匹配
    boolean regexp() default false;

    //是否开启偏移量，如果开启则根据title的值进行偏移
    boolean related() default false;

    //偏移量
    int relatedOffset() default 0;

    //排序
    int sort() default 1;

    //行坐标
    int rowIndex() default 0;

    //列坐标
    int colIndex() default -1;


    //定义是否必须
    boolean need() default true;

    //定义处理超链接的处理类，须实现HyperLinkStyle接口
    String hyperlink() default "";


    //定义处理值转换的处理类，须实现IConvert接口
    String convert() default "";
}
