package com.ustc.plugins.model;

import com.ustc.plugins.style.HyperLinkStyle;
import com.ustc.plugins.style.IConvert;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/9/17<br>
 * filename: TitleInfo<br>
 * <p>
 * description:<br>
 * excel标题属性定义
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
@Data
public class TitleInfo {

    /**
     * 标题
     */
    private String title;

    /**
     * 是否开启正则表达式
     */
    private boolean regexp;

    /**
     * 是否开启关联
     */
    private boolean related;

    /**
     * 关联的偏移量
     */
    private Integer relatedOffset;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 行坐标
     */
    private Integer rowIndex;

    /**
     * 列坐标
     */
    private Integer colIndex;

    /**
     * 列
     */
    private Integer colspan;
    /**
     * 行
     */
    private Integer rowspan;

    /**
     * 字段
     */
    private Field field;

    /**
     * 设置值方法 入参只能且只有String.class
     */
    private Method setMethod;

    /**
     * 超链接
     */
    private HyperLinkStyle hyperlink;

    /**
     * 值转换
     */
    private IConvert convert;

    /**
     * 读取取方法
     */
    private Method getMethod;

    /**
     * 是否必须字段
     */
    private boolean need;


    /**
     * 设置值方法
     *
     * @param value    值
     * @param instance 实例对象
     * @param <T>      泛型
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public <T> void setValue(String value, T instance) throws InvocationTargetException, IllegalAccessException {
        if (need) {
            Assert.isTrue(!StringUtils.isEmpty(value), title + "不能为空");
        }
        setMethod.invoke(instance, value);
    }


    /**
     * 获取值方法
     *
     * @param instance 实例对象
     * @param <T>      泛型
     * @return 值
     */
    public <T> Object getValue(T instance) {
        try {
            return getMethod.invoke(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 值转换接口
     *
     * @param value 值
     * @return 转换值
     */
    public String convert(String value) {
        if (convert != null) {
            return convert.covert(value);
        }
        return value;
    }

    /**
     * 设置超链接
     *
     * @param value 值
     * @return 超链接
     */
    public String setHyperLinkStyle(String value) {
        return hyperlink.setHyperLinkStyle(value);
    }
}
