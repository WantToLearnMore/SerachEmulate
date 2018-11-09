package com.emulate.search.utils;

import com.alibaba.fastjson.JSONObject;
import com.emulate.search.config.WebbenchConfig;
import com.emulate.search.po.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultUtil {

    private final static Logger logger =  LoggerFactory.getLogger(ResultUtil.class);

    /**
     * 读取服务器当前的IO信息
     * */
    public static List<Map<String,String>> obtainIOInfo(){

        try {
            String[] cmd = new String[] { "/bin/sh", "-c", "iostat -d -k" };
            Process ps = Runtime.getRuntime().exec(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            List<Map<String,String>> elements=new ArrayList<>();
            int i=0;
            while ((line = br.readLine()) != null) {
                if(i>2){
                    String li=line.replaceAll(" +",",");
                    String[]rs=li.split(",");
                    if(rs.length==6){
                        Map<String ,String >map=new HashMap<>();
                        map.put("device",rs[0]);
                        map.put("tps",rs[1]);
                        map.put("kb_read/s",rs[2]);
                        map.put("kb_write/s",rs[3]);
                        map.put("kb_read",rs[4]);
                        map.put("kb_write",rs[5]);
                        System.out.println(map);
                        elements.add(map);
                    }
                }
                i++;
            }
            return elements;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,String>benchStart(String url){

        WebbenchConfig webbenchConfig=WebbenchConfig.getInstance();
        BufferedReader br=null;
        Map<String,String>result=new HashMap<>();
        try {
            List<String> convert = new ArrayList<String>();
            convert.add(webbenchConfig.getDevicePath());
            convert.add("-c");
            convert.add(webbenchConfig.getClientNum());
            convert.add("-t");
            convert.add(webbenchConfig.getTime());
            convert.add(url);
            Process p = new ProcessBuilder().command(convert).redirectErrorStream(true).start();
            p.waitFor();

            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            List<String>eles=new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                eles.add(line);
            }

            if(eles.size()>0){
                String []strs=eles.get(eles.size()-1).split(":");
                result.put("速度",strs[1]);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

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
        jsonObject.put("rows",data);
        jsonObject.put("msg",msg);
        jsonObject.put("total",10);
        jsonObject.put("time",time);
        return jsonObject.toJSONString();
    }

    public static JSONObject buildJSON(String status,String msg,String time,Object data){

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("rows",data);
        jsonObject.put("msg",msg);
        jsonObject.put("total",10);
        jsonObject.put("time",time);
        return jsonObject;
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

}
