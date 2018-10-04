package com.emulate.search.utils;

public class FormatUtil {


    public static String  test(String key,String tableName,String ...fields){
        StringBuilder sql=new StringBuilder("");
        sql.append("select ");
        sql.append(ObjectUtil.buildFieldString(fields));
        sql.append(tableName);
        sql.append(" where Locate (");
        sql.append(key);
        sql.append(",");
        sql.append("name");
        sql.append(")");
        return  sql.toString();
    }
    public static void main(String []args){

      String str=  test("111","video");

        System.out.println(str);
    }
}
