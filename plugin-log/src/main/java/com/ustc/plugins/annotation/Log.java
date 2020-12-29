package com.ustc.plugins.annotation;

import com.ustc.plugins.OperType;

import java.lang.annotation.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: Log<br>
 * <p>
 * description:<br>
 * 操作日志标识注解
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
    String value();

    OperType operType();
}
