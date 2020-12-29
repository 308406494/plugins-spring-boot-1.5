package com.ustc.plugins.model;

import lombok.Data;

import java.util.Date;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/7/3<br>
 * filename: OperLog<br>
 * <p>
 * description:<br>
 * 操作日志
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version 1.0
 */
@Data
public class LogInfo {

    /**
     * 操作员
     */
    private String operator;
    /**
     * 操作类型
     */
    private String operType;

    /**
     * 平台
     */
    private String plat;
    /**
     * 操作
     */
    private String operate;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * ip
     */
    private String ip;
    /**
     * 请求
     */
    private String request;
    /**
     * 返回
     */
    private String response;
}
