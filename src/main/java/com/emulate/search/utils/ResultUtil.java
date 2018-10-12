package com.emulate.search.utils;

import com.alibaba.fastjson.JSONObject;
import com.emulate.search.po.Video;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResultUtil {

    /**
     * 封装返回结果
     * @param status 状态码
     * @param msg  提示信息
     * @param data 结果数据
     * @param time 执行时间
     * */
    public static String buildJSONResult(String status,String msg,String time,Object data){

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("row",data);
        jsonObject.put("msg",msg);
        jsonObject.put("total",10);
        jsonObject.put("time",time);
        return jsonObject.toJSONString();
    }

    public static List<Video> ReadDataFromFile(String path){
        FileInputStream file=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        String str=null;
        Video video=null;
        List<Video> videos=new ArrayList<>();
        try {
            file=new FileInputStream(path);
            inputStreamReader=new InputStreamReader(file);
            bufferedReader=new BufferedReader(inputStreamReader);
            int j=0;
            while ((str=bufferedReader.readLine())!=null){
                String []data=str.split(",");
                if(data.length>=3){
                    video=new Video();
                    String name=data[0].replace("\t","");
                    video.setName(name);
                    video.setAuthor(data[1]+","+data[2]);
                    video.setUrl("source/video/");
                    videos.add(video);
                }
                if(data.length<3){
                    System.out.println(j);
                    for (String s:data) {
                        System.out.print(s);
                    }
                   System.out.println();
                }
                j++;
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
        return videos;
    }

    public static List<Video> ReadDataFromFileTX(String path){
        FileInputStream file=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        String str=null;
        Video video=null;
        List<Video> videos=new ArrayList<>();
        try {
            file=new FileInputStream(path);
            inputStreamReader=new InputStreamReader(file);
            bufferedReader=new BufferedReader(inputStreamReader);
            int j=0;
            while ((str=bufferedReader.readLine())!=null){
                String []data=str.split(",");
                videos.add(buildVideoFromTX(data));
                j++;
                System.out.println("一共有"+j+"条数据");
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
        return videos;
    }
    private static Video buildVideoFromTX(String [] data){
        Video video=null;
        if(data[0].contains("description")){
             String name=data[0].replace(" description","");
             name=name.replace("\t","");
            video=new Video();
            video.setName(name);
            video.setUrl("source/video/");
            video.setDescription(data[1]);
            String author="";
            if(data.length>=3){
                for (int i=2;i<data.length;i++){
                    author=author+data[i]+"/";
                }
                author=author.substring(0,author.length()-1);

            }else {
                author="未知";
            }
            video.setAuthor(author);
        }else {
            video=new Video();
            String name=data[0].replace("\t","");
            video.setName(name);
            video.setUrl("source/video/");
            String author="";
            if(data.length>=2){
                for(int i=1;i<data.length;i++){
                    author=author+data[i]+"/";
                }
                author=author.substring(0,author.length()-1);

            }else {
                author="未知";
            }
            video.setAuthor(author);
        }
        return video;
    }

    public static void main(String []args){

        List<Video>videos=ResultUtil.ReadDataFromFileTX("D:\\data0\\txMovie.txt");
        System.out.println(videos.size());
        System.out.println("第450条信息:"+videos.get(449));
        for (Video v:videos) {
            System.out.println(ObjectUtil.objectToMap(v));
        }


    }
}
