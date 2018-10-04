package com.emulate.search.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

public class ObjectUtil {

    //将对象转化为map
    public static Map<String,Object> objectToMap(Object object){
        return (Map<String,Object>)JSON.toJSON(object);
    }

    //将map转换位对象
    public static <T>T mapToObject(Map<String,Object>map,Class<T>T){
        return JSON.parseObject(JSON.toJSONString(map),T);
    }

    //构造字段字符串，为多字段查询提供便利
    public static String buildFieldString(String[] fields){
        StringBuilder fieldBuilder = new StringBuilder("");
        if(fields==null||fields.length==0){
            fieldBuilder.append("*");
        }else {
            for(String field: fields){
                fieldBuilder.append("`").append(field).append("`,");
            }
            fieldBuilder.deleteCharAt(fieldBuilder.length()-1);
        }
        return fieldBuilder.toString();
    }

    public static String buildFieldString(List<String>fields){
        StringBuilder stringBuilder=new StringBuilder("");
        if(fields.isEmpty()){
            stringBuilder.append("*");
        }else {
            for (String field:fields){
                stringBuilder.append("`").append(field).append("`,");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }

        return stringBuilder.toString();
    }


    /**
     * 构建通用sql
     * @param fields
     * @param op sql类型,可选值：select，update，remove
     * @param condition  可选值：where limit locate position groupby orderby
     * @param tableName 表名
     * @return
     * */
    public static String buildSql(String []fields,String op,String condition,String  ...tableName){

        StringBuilder sql=new StringBuilder("");

        sql.append("select ");
        sql.append(buildFieldString(fields));
        sql.append(" from ");
        sql.append(buildTableString(tableName));
        sql.append(condition);
        return sql.toString();
    }

    private static String buildTableString(String[]fields){
        StringBuilder table= new StringBuilder("");
        if(fields.length==0||fields==null){

        }else {
            for (String field:fields) {
                table.append(field).append(",");
            }

            table.deleteCharAt(table.length()-1);
        }
        return table.toString();
    }

    public static <T>T buildObject(Object object,Class<T>T){

        return JSON.parseObject(JSON.toJSONString(object),T);
    }

}
