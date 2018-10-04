package com.emulate.search.dao.mysql;

import java.util.List;
import java.util.Map;

public interface CommonDao {

    /**
     * 根据id号查询信息
     * @param id id号
     * @param tableName 表名
     * @param T 资源类型
     * @param fields 可变参数，属性名
     * @return 返回一条目标资源
     * */
    public <T>T selectById(int id,String tableName,Class<T>T,String ...fields);

    /**
     * 根据id号查询资源
     * @param id 资源在数据库中的id号
     * @param tableName 数据表的名字
     * @param T 资源类型
     * @param fields 需要查询的属性，以List封装
     * @return
     * */
    public <T>T selectById(int id,String tableName,Class<T>T,List<String>fields);

    /**
     * 模糊查询
     * @param key 关键字
     * @param scope 搜索的范围，即以那个字段为标准查询
     * @param tableName 表名
     * @param fields 结果需要的字段 使用可变参数，适用于字段少时
     * @return list 查询结果
     * */
    public List <Map<String,Object>>selectSourceLike(String key, String scope,String tableName, String ...fields);

    /**
     * 模糊查询
     * @param key 关键字
     * @param scope 搜索的范围，即以那个字段为标准查询
     * @param tableName 表名
     * @param fields 结果需要的字段 使用可变参数，适用于字段多时
     * @return list 查询结果
     * */
    public List <Map<String,Object>>selectSourceLike(String key, String scope,String tableName, List<String>fields);


    /**
     * 计数
     *
     * */
    public int countSource(String tableName);
}
