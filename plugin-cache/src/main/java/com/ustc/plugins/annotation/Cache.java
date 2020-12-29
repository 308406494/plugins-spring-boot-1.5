package com.ustc.plugins.annotation;

import com.ustc.plugins.service.IRedisService;

import java.lang.annotation.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: Cache<br>
 * <p>
 * description:<br>
 * 该注解主要用于将当前方法的执行结果放入redis中，如果查询条件如果有缓存则返回缓存数据，否则进入执行逻辑并保存到redis中
 * value 指定保存的类型 DAY WEEK MONTH TMP SYS
 * DAY: 表示只缓存一天，默认0点过期
 * WEEK: 表示最多缓存一再，默认下周一0点清除，可通过dayOfWeek设置
 * MONTH: 表示最多缓存一个月，默认下个月0点清除，可通过dayOfMonth设置
 * TMP:  0点过期
 * SYS: 不过期
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
    IRedisService.TYPE value() default IRedisService.TYPE.SYS;

    int dayOfMonth() default 1;

    //0星期六 1星期天 2星期一
    int dayOfWeek() default 2;

    int hour() default 0;

    int minute() default 0;

    int second() default 0;

    //是否开户个性化缓存
    boolean personal() default false;
}
