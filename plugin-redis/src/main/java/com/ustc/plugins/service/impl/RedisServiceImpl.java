package com.ustc.plugins.service.impl;

import com.ustc.plugins.DateUtils;
import com.ustc.plugins.service.IRedisService;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ustc.plugins.service.IRedisService.TYPE.*;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/3/20<br>
 * filename: RedisServiceImpl<br>
 * <p>
 * description:<br>
 * redis 实现方法
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 * @version ${version}
 */
@Data
public class RedisServiceImpl implements IRedisService {

    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 指定过期日期的设置值
     *
     * @param type 类型 DAY WEK MON TMP SYS
     * @param key  key
     * @param obj  对象
     * @param date 过期时间
     */
    @Override
    public <T> void save(TYPE type, String key, T obj, Date date) {
        redisTemplate.opsForValue().set(type + SPLIT + key, obj);
        expireAt(type, key, date);
    }


    /**
     * 指定过期日期的设置值
     *
     * @param type 类型 DAY WEK MON TMP SYS
     * @param key  key
     * @param obj  对象
     */
    @Override
    public <T> void save(TYPE type, String key, T obj) {
        redisTemplate.opsForValue().set(type + SPLIT + key, obj);
    }

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     */
    @Override
    public <T> void saveDay(String key, T obj) {
        save(DAY, key, obj, DateUtils.nextDay());
    }

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     */
    @Override
    public <T> void saveWeek(String key, T obj) {
        save(WEK, key, obj, DateUtils.nextWeek());
    }

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     */
    @Override
    public <T> void saveMonth(String key, T obj) {
        save(MON, key, obj, DateUtils.nextMonth());
    }

    /**
     * 根据key保存value
     *
     * @param key        key
     * @param obj        对象
     * @param expireTime 过期时间
     */
    @Override
    public <T> void saveTmp(String key, T obj, int expireTime) {
        redisTemplate.opsForValue().set(TMP + SPLIT + key, obj, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 根据key保存value
     *
     * @param key  key
     * @param obj  对象
     * @param date 过期时间
     */
    @Override
    public <T> void saveTmp(String key, T obj, Date date) {
        save(TMP, key, obj, date);
    }

    /**
     * 根据key保存value
     *
     * @param key key
     * @param obj 对象
     */
    @Override
    public <T> void saveSys(String key, T obj) {
        redisTemplate.opsForValue().set(SYS + SPLIT + key, obj);
    }


    /**
     * 根据key 删除数据
     *
     * @param type type
     * @param key  key
     */
    @Override
    public void delete(TYPE type, String key) {
        redisTemplate.delete(type + SPLIT + key);
    }

    /**
     * 根据key 删除数据
     *
     * @param type    type
     * @param pattern pattern
     */
    @Override
    public void deletes(TYPE type, String pattern) {
        Set<String> keys = redisTemplate.keys(type + SPLIT + pattern + "*");
        redisTemplate.delete(keys);
    }

    /**
     * 根据key检查是否存在
     *
     * @param type type
     * @param key  key
     * @return true 存在 false 不存在
     */
    @Override
    public boolean exists(TYPE type, String key) {
        return redisTemplate.hasKey(type + SPLIT + key);
    }

    /**
     * 在指定时间失效
     *
     * @param type type
     * @param key  key
     * @param date 日期
     */
    @Override
    public void expireAt(TYPE type, String key, Date date) {
        redisTemplate.expireAt(type + SPLIT + key, date);
    }

    /**
     * 自动增长
     *
     * @param key   key
     * @param delta 增加值
     * @return 增加后值
     */
    @Override
    public Integer increment(String key, Integer delta) {
        return redisTemplate.opsForValue().increment(key, delta.longValue()).intValue();
    }

    /**
     * 根据key获取value
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @return value
     */
    @Override
    public String get(TYPE type, String key) {
        return redisTemplate.opsForValue().get(type + SPLIT + key).toString();
    }

    /**
     * 对象不存在则返回默认值
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @param obj  默认值
     * @return 值
     */
    @Override
    public String getDefault(TYPE type, String key, String obj) {
        if (exists(type, key)) {
            return get(type, key);
        }
        return obj;
    }

    /**
     * 根据key获取value
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @return 泛型对象
     */
    @Override
    public <T> T get(TYPE type, String key, Class<T> aClass) {
        return (T) redisTemplate.opsForValue().get(type + SPLIT + key);
    }

    /**
     * 保存数组
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @param data 数据
     */
    @Override
    public <T> void saveList(TYPE type, String key, List<T> data) {
        redisTemplate.opsForList().leftPushAll(type + SPLIT + key, data);
    }


    /**
     * 根据索引查找数据
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param index  索引
     * @return 数据
     */
    @Override
    public <T> T index(TYPE type, String key, Class<T> aClass, int index) {
        return (T) redisTemplate.opsForList().index(type + SPLIT + key, index);
    }

    /**
     * 返回数组长度
     *
     * @param type 类型 TMP, SYS
     * @param key  key
     * @return 长度
     */
    @Override
    public int size(TYPE type, String key) {
        return redisTemplate.opsForList().size(type + SPLIT + key).intValue();
    }

    /**
     * 截取数据
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @param start  开始索引
     * @param end    结束索引
     * @return 截取数据泛型
     */
    @Override
    public <T> List<T> range(TYPE type, String key, Class<T> aClass, int start, int end) {
        return (List<T>) redisTemplate.opsForList().range(type + SPLIT + key, start, end);
    }

    /**
     * 根据key获取value
     *
     * @param type   类型 TMP, SYS
     * @param key    key
     * @param aClass class
     * @return 泛型对象
     */
    @Override
    public <T> List<T> getList(TYPE type, String key, Class<T> aClass) {
        return range(type, key, aClass, 0, size(type, key));
    }

    /**
     * 向redis消息队列中发送数据 数据大小不应超过10kBytes
     *
     * @param topic   主题
     * @param message 消息内容
     */
    @Override
    public void sendMessage(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }

    /**
     * 队列中加入消息
     *
     * @param key key
     * @param obj message
     */
    @Override
    public <T> void push(String key, T obj) {
        redisTemplate.opsForList().leftPush(QUE + SPLIT + key, obj);
    }

    /**
     * 队列中取出消息
     *
     * @param key    key
     * @param tClass 类
     * @return message
     */
    @Override
    public <T> T pop(String key, Class<T> tClass) {
        return (T) redisTemplate.opsForList().rightPop(QUE + SPLIT + key);
    }

    /**
     * 队列中剩余长度
     *
     * @param key key
     * @return 长度
     */
    @Override
    public int remain(String key) {
        return redisTemplate.opsForList().size(QUE + SPLIT + key).intValue();
    }
}
