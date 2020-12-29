package com.ustc.plugins;


import com.ustc.plugins.annotation.Cache;
import com.ustc.plugins.annotation.Clear;
import com.ustc.plugins.service.IRedisService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: OperationLogAspect<br>
 * <p>
 * description:<br>
 * 接口日志切面类
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Aspect
@Order(1)
@Component
@Log4j
public class CacheAspect {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private IPersonal personal;


    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("@annotation(com.ustc.plugins.annotation.Cache)")
    public void cacheJoinPoint() {
    }


    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoint 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     *
     * @param point 切入点信息
     */
    @Around(value = "cacheJoinPoint() && @annotation(annotation)", argNames = "point, annotation")
    public Object cacheMethod(ProceedingJoinPoint point, Cache annotation) throws Throwable {
        Object[] args = point.getArgs();
        Object target = point.getTarget();
        Signature signature = point.getSignature();
        StringBuilder sb = new StringBuilder();
        IRedisService.TYPE type = annotation.value();
        if (annotation.personal()) {
            sb.append(personal.person()).append(IRedisService.SPLIT);
        }
        sb.append(target.getClass().getName()).append(IRedisService.SPLIT)
                .append(signature.getName()).append(IRedisService.SPLIT);
        for (Object arg : args) {
            sb.append(arg).append(IRedisService.SPLIT);
        }
        String key = sb.substring(0, sb.length() - 1);

        if (redisService.exists(type, key)) {
            return redisService.get(type, key, Object.class);
        } else {
            Object proceed = point.proceed();
            int hour = annotation.hour();
            int minute = annotation.minute();
            int second = annotation.second();
            int dayOfWeek = annotation.dayOfWeek();
            int dayOfMonth = annotation.dayOfMonth();
            Date date;
            switch (type) {
                case DAY:
                    date = DateUtils.nextDay(hour, minute, second);
                    redisService.save(type, key, proceed, date);
                    break;
                case WEK:
                    date = DateUtils.nextWeek(dayOfWeek, hour, minute, second);
                    redisService.save(type, key, proceed, date);
                    break;
                case MON:
                    date = DateUtils.nextMonth(dayOfMonth, hour, minute, second);
                    redisService.save(type, key, proceed, date);
                    break;
                case TMP:
                    date = DateUtils.nextDay();
                    redisService.save(type, key, proceed, date);
                    break;
                case SYS:
                    redisService.saveSys(key, proceed);
                    break;
                default:
            }
            return proceed;
        }
    }

    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("@annotation(com.ustc.plugins.annotation.Clear)")
    public void clearJoinPoint() {
    }

    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoint 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     *
     * @param point 切入点信息
     */
    @Around(value = "clearJoinPoint() && @annotation(annotation)", argNames = "point, annotation")
    public Object clearMethod(ProceedingJoinPoint point, Clear annotation) throws Throwable {
        Object target = point.getTarget();
        Class<?> tClass = target.getClass();
        Method[] declaredMethods = tClass.getDeclaredMethods();
        String className = tClass.getName();

        for (Method declaredMethod : declaredMethods) {
            Cache cache = declaredMethod.getAnnotation(Cache.class);
            if (cache != null) {
                if (cache.personal()) {
                    redisService.deletes(cache.value(), personal.person() + IRedisService.SPLIT + className + IRedisService.SPLIT + declaredMethod.getName());
                } else {
                    redisService.deletes(cache.value(), className + IRedisService.SPLIT + declaredMethod.getName());
                }
            }
        }
        return point.proceed();
    }

}
