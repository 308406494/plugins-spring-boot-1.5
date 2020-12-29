package com.ustc.plugins.service;

import java.util.List;

/**
 * company: guochuang software co.ltd<br>
 * date: 2020/8/12<br>
 * filename: IFilterData<br>
 * <p>
 * description:<br>
 * 过滤接口
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
public interface IFilterData<T> {

    /**
     * 数据过滤方法
     *
     * @param data   数据
     * @param result 结果
     * @param error 错误信息
     * @return 数据
     */
    T filter(T data, List<T> result, StringBuilder error);
}
