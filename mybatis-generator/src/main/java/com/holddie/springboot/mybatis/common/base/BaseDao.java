package com.holddie.springboot.mybatis.common.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * code is far away from bug with the animal protecting
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * 　　┃　　　┃神兽保佑
 * 　　┃　　　┃代码无BUG！
 * 　　┃　　　┗━━━┓
 * 　　┃　　　　　　　┣┓
 * 　　┃　　　　　　　┏┛
 * 　　┗┓┓┏━┳┓┏┛
 * 　　　┃┫┫　┃┫┫
 * 　　　┗┻┛　┗┻┛
 * ---------------------------------
 * @author HoldDie
 * @version v1.0.0
 * @description Dao层基类
 * @since 2018/5/20 22:11
 */
public interface BaseDao<T> extends BaseMapper<T> {

    List<T> selectPageWithParam(Page<T> page, T param);

    List<T> selectPageWithMap(Page<T> page, Map<String, Object> map);

    List<T> selectListWithParam(T param);

    List<T> selectListWithMap(Map<String, Object> map);

    List<Map<String, Object>> selectListMapWithParam(T param);

    List<Map<String, Object>> selectListMapWithMap(Map<String, Object> map);

    List<Object> selectListObjWithParam(T param);

    List<Object> selectListObjWithMap(Map<String, Object> map);

    Map<String, Object> selectMapWithParam(T param);

    Map<String, Object> selectMapWithMap(Map<String, Object> map);

    Object selectObjWithParam(T param);

    Object selectObjWithMap(Map<String, Object> map);

    T selectOneWithParam(T param);

    T selectOneWithMap(Map<String, Object> map);

    T selectOneByObj(Serializable var1);

}
