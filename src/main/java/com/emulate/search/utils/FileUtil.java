package com.emulate.search.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emulate.search.common.Context;
import com.emulate.search.po.Video;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static String ReadFileByRealPath(String path){
        FileInputStream file=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        String str=null;
        try {
            file=new FileInputStream(path);
            inputStreamReader=new InputStreamReader(file);
            bufferedReader=new BufferedReader(inputStreamReader);
            while ((str=bufferedReader.readLine())!=null){

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return str;
    }

    //构造音频或视频文件在文件系统中的路径
    public static String buildFilePath(Map<String,Object>map){
        String sourceType=String.valueOf(map.get("sourceType"));
        String type=String.valueOf(map.get("type"));
        String sourceName=String.valueOf(map.get("name"));
        String filePath= Context.ContentPath+"//"+sourceType+"//"+type+"//"+sourceName;
        return filePath;
    }

    //将文件存入文件系统
    public static Map<String,Object>saveFile(String filePath,File file){

        //判断该文件路劲是否存在，若不存在就创建
        if (file.isDirectory()){

        }
        Map<String,Object>map=new HashMap<>();

        return map;
    }





    public static  void main(String [] args){

        List<String>ls=new ArrayList<>();
        for(int i=0;i<10;i++){
            ls.add("第："+String.valueOf(i));
        }
        String json= JSONObject.toJSONString(ls);
        System.out.println(json);
        List<String> list= JSON.parseArray(json,String.class);

        for (String s:list) {
            System.out.println(s);
        }

    }
}
