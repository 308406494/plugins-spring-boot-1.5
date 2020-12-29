package com.ustc.plugins.service;

import java.util.Date;
import java.util.List;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/3/20<br>
 * filename: IRedisService<br>
 * <p>
 * description:<br>
 * ${DESCRIPTION}
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
public interface IRedisService {
    enum TYPE {
        //明天的0点自动清除
        DAY,
        //下周的0点自动清除
        WEK,
        //下个月的0点自动清除
        MON,
        //临时数据
        TMP,
        //不过期数据，长期保存
        SYS,
        //队列
        QUE
    }

    String SPLIT = ":";

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     * @param <T> 类型说明
     */
    <T> void saveDay(String key, T obj);

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     * @param <T> 类型说明
     */
    <T> void saveWeek(String key, T obj);

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     * @param <T> 类型说明
     */
    <T> void saveMonth(String key, T obj);

    /**
     * 根据key保存value
     *
     * @param key        key
     * @param obj        对象
     * @param expireTime 过期时间长
     * @param <T>        类型说明
     */
    <T> void saveTmp(String key, T obj, int expireTime);

    /**
     * 根据key保存value
     *
     * @param key  key
     * @param obj  对象
     * @param date 过期时间
     * @param <T>  类型说明
     */
    <T> void saveTmp(String key, T obj, Date date);

    /**
     * 指定过期日期的设置值
     *
     * @param type 类型 DAY WEK MON TMP SYS
     * @param key  key
     * @param obj  对象
     * @param date 过期时间
     * @param <T>  泛型
     */
    <T> void save(TYPE type, String key, T obj, Date date);

    /**
     * 指定过期日期的设置值
     *
     * @param type 类型 DAY WEK MON TMP SYS
     * @param key  key
     * @param obj  对象
     * @param <T>  泛型
     */
    <T> void save(TYPE type, String key, T obj);


    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     * @param <T> 类型说明
     */
    <T> void saveSys(String key, T obj);

    /**
     * 自动增长
     *
     * @param key   key
     * @param delta 增加值
     * @return 增加后值
     */
    Integer increment(String key, Integer delta);

    /**
     * 根据key 删除数据
     *
     * @param type type
     * @param key  key
     */
    void delete(TYPE type, String key);

    /**
     * 根据key 删除数据
     *
     * @param type    type
     * @param pattern pattern
     */
    void deletes(TYPE type, String pattern);

    /**
     * 根据key检查是否存在
     *
     * @param type type
     * @param key  key
     * @return true 存在 false 不存在
     */
    boolean exists(TYPE type, String key);

    /**
     * 在指定时间失效
     *
     * @param type 类型
     * @param key  key
     * @param date 日期
     */
    void expireAt(TYPE type, String key, Date date);


    /**
     * 根据key获取value
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @return value
     */
    String get(TYPE type, String key);

    /**
     * 对象不存在则返回默认值
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @param obj  默认值
     * @return 值
     */
    String getDefault(TYPE type, String key, String obj);


    /**
     * 根据key获取value
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param <T>    泛型
     * @return 泛型对象
     */
    <T> T get(TYPE type, String key, Class<T> aClass);


    /**
     * 保存不过期的数组
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @param data 数据
     * @param <T>  泛型
     */
    <T> void saveList(TYPE type, String key, List<T> data);


    /**
     * 根据索引查找数据
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param index  索引
     * @param <T>    泛型
     * @return 数据
     */
    <T> T index(TYPE type, String key, Class<T> aClass, int index);


    /**
     * 返回数组长度
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @return 长度
     */
    int size(TYPE type, String key);

    /**
     * 截取数据
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param start  开始索引
     * @param end    结束索引
     * @param <T>    泛型
     * @return 截取数据泛型
     */
    <T> List<T> range(TYPE type, String key, Class<T> aClass, int start, int end);

    /**
     * 根据key获取value
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param <T>    泛型
     * @return 泛型对象
     */
    <T> List<T> getList(TYPE type, String key, Class<T> aClass);


    /**
     * 向redis消息队列中发送数据 数据大小不应超过10kBytes
     *
     * @param topic   主题
     * @param message 消息内容
     */
    void sendMessage(String topic, Object message);

    /**
     * 队列中加入消息
     *
     * @param key key
     * @param obj message
     */
    <T> void push(String key, T obj);

    /**
     * 队列中取出消息
     *
     * @param key    key
     * @param tClass 类
     * @return message
     */
    <T> T pop(String key, Class<T> tClass);


    /**
     * 队列中剩余长度
     *
     * @param key key
     * @return 长度
     */
    int remain(String key);


}
