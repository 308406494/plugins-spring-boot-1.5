package com.ustc.plugins.service;

import java.util.List;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/11/26<br>
 * filename: IFetchData<br>
 * <p>
 * description:<br>
 * 取数据接口
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
public interface IFetchData<T> {

    /**
     * 截取数据
     *
     * @param size  取数量
     * @param cycle 轮数
     * @return 数据
     */
    List<T> subData(int cycle, int size);
}
