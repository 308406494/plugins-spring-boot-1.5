package com.ustc.plugins.service;

import com.ustc.plugins.model.LogInfo;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * company: guochuang software co.ltd<br>
 * date: 2020/2/19<br>
 * filename: ILogService<br>
 * <p>
 * description:<br>
 * Log的服务接口
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
public interface ILogService {

    /**
     * 获取用户名
     *
     * @param point 切入点
     * @return 用户名
     */
    String getUserName(ProceedingJoinPoint point);


    /**
     * 保存日志信息
     *
     * @param log 日志对象
     */
    void saveLog(LogInfo log);

    /**
     * 获取平台
     *
     * @return 平台
     */
    String getPlat();
}
