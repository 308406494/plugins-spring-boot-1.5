package com.ustc.plugins.annotation;

import java.lang.annotation.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: Log<br>
 * <p>
 * description:<br>
 * 分页标识注解 作用于方法上
 * 方法固定前三个参数
 * int pageNum, int pageSize, AtomicInteger total
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Page {
}
