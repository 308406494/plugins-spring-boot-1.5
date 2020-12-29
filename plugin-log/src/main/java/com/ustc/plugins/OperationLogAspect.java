package com.ustc.plugins;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ustc.plugins.annotation.Log;
import com.ustc.plugins.model.LogInfo;
import com.ustc.plugins.service.ILogService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
@Log4j2
public class OperationLogAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ILogService logService;

    /**
     * 定义一个方法，用于声明切入点表达式，方法中一般不需要添加其他代码
     * 使用@Pointcut声明切入点表达式
     * 后面的通知直接使用方法名来引用当前的切点表达式；如果是其他类使用，加上包名即可
     */
    @Pointcut("@annotation(com.ustc.plugins.annotation.Log)")
    public void declareJoinPointExpression() {
    }

    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoin 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     *
     * @param point 切入点信息
     */
    @Around(value = "declareJoinPointExpression() && @annotation(annotation)", argNames = "point, annotation")
    public Object aroundMethod(ProceedingJoinPoint point, Log annotation) throws Throwable {
        LogInfo logInfo = new LogInfo();
        logInfo.setOperateTime(new Date());
        logInfo.setOperator(logService.getUserName(point));
        logInfo.setPlat(logService.getPlat());
        logInfo.setIp(request.getRemoteHost());

        logInfo.setOperType(annotation.operType().toString());
        logInfo.setOperate(annotation.value());
        Map map = request.getParameterMap();
        String queryString;
        if (map.isEmpty()) {
            queryString = request.getQueryString();
            if (StringUtils.isEmpty(queryString)) {
                List<Object> list = Arrays.asList(point.getArgs());
                List<Object> objList = new ArrayList<>();
                list.forEach(ele -> {
                    if (ele instanceof MultipartFile[]) {
                    } else {
                        objList.add(ele);
                    }
                });
                queryString = JSONArray.toJSONString(objList);
            }
        } else {
            queryString = JSONObject.toJSONString(map);
        }
        if (!StringUtils.isEmpty(queryString)) {
            logInfo.setRequest(URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString()));
        }
        Object result = null;
        try {
            result = point.proceed();
            String response = JSONObject.toJSONString(result);
            int length = 3000;
            if (response.length() > length) {
                response = response.substring(0, length);
            }
            logInfo.setResponse(response);
        } catch (Exception e) {
            log.error(logInfo.getOperate() + "出错！" + e.getMessage(), e);
            logInfo.setResponse(e.getMessage());
        }
        logService.saveLog(logInfo);
        return result;
    }
}
