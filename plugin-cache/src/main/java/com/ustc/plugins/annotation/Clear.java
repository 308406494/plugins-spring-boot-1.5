package com.ustc.plugins.annotation;

import java.lang.annotation.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: Clear<br>
 * <p>
 * description:<br>
 * 该注解主要用于在执行方法之前，删除该类方法上所有的缓存信息
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Clear {
}
