package com.ustc.plugins;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/4/4<br>
 * filename: PageHelperAspect<br>
 * <p>
 * description:<br>
 * 获取分页数据切面类
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Aspect
@Order(2)
public class PageHelperAspect {

    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("@annotation(com.ustc.plugins.annotation.Page)")
    public void declareJoinPointExpression() {
    }

    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoint 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     *
     * @param point 切入点信息
     */
    @Around(value = "declareJoinPointExpression()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        int pageNum = (int) args[0];
        int pageSize = (int) args[1];
        AtomicInteger total = (AtomicInteger) args[2];
        PageHelper.startPage(pageNum, pageSize);
        List proceed = (List) point.proceed();
        PageInfo pageInfo = new PageInfo(proceed);
        total.set(Math.toIntExact(pageInfo.getTotal()));
        return proceed;
    }

}
