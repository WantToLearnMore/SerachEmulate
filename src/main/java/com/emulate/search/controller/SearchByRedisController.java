package com.emulate.search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.Video;
import com.emulate.search.service.RedisService;
import com.emulate.search.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("searchByRedis")
public class SearchByRedisController {

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private RedisService redisService;

    /**
     * 所有包含关键字的视频或音频文件
     * @param keywords
     * @param author
     * @param operition
     * @return
     * */
    @GetMapping("/search")
    public String searchSource(@RequestParam("keywords")String keywords,@RequestParam("author")String author,@RequestParam("operition")String operition){

        List<Map<String,Object>>videos=new ArrayList<>();
        List<Map<String,Object>>audios=new ArrayList<>();
        String msg="";


        boolean hasAudio=redisService.exists(Context.audioKey.concat(keywords));

        if (hasAudio){
           audios.add((Map<String,Object>) redisService.get(Context.audioKey.concat(keywords)));
           msg="本次查询的音频数据来自redis缓存！！！";
        }else {
            //如果缓存中没有，就从数据库中查询，若数据库中有就返回并将数据添加入缓存，若数据库中没有就返回无该类数据
            audios=commonDao.selectSourceLike(keywords,"name","audio");
            if(!(audios==null||audios.isEmpty())){
                hasAudio=true;
                msg="本次查询的音频数据来自数据库！！！";
                for (Map<String,Object>map:audios) {
                    //将数据写入缓存，有效时间为10分钟
                    redisService.set(Context.audioKey.concat(String.valueOf(map.get("name"))),map,10L,TimeUnit.MINUTES);
                    System.out.println(Context.audioKey.concat(String.valueOf(map.get("name"))));
                }
            }
        }
        boolean hasVideo=redisService.exists(Context.videoKey.concat(keywords));//判断缓存中是否存在
        System.out.println(Context.videoKey.concat(keywords));
        if(hasVideo){
            videos.add((Map<String,Object>) redisService.get(Context.videoKey.concat(keywords)));
            msg=msg+"本次查询的视频数据来自redis缓存！！！";
        }else {
            videos=commonDao.selectSourceLike(keywords,"name","video");
            if(!(videos.isEmpty()||videos==null)){
                hasVideo=true;
                msg=msg+"本次查询的视频数据来自数据库！！！";
                for (Map<String,Object>map:videos) {
                    System.out.println(Context.videoKey.concat(String.valueOf(map.get("name"))));
                    redisService.set(Context.videoKey.concat(String.valueOf(map.get("name"))),map,10L,TimeUnit.MINUTES);
                }
            }
        }

        Map<String,Object>result=new HashMap<>();
        String status=Status.FAIL;
        String msge=Context.NoSuchFile;
        System.out.println(hasAudio||hasVideo);
        if(hasAudio||hasVideo){
           status=Status.SUCCESS;
           msge=msg;
        }

        result.put("video",videos);
        result.put("audio",audios);
        return ResultUtil.buildJSONResult(status,msge,result);
    }


    /**
     * @param keywords 关键字，目前为资源名称
     * @param type 目标资源类型，目前可选值：video，audio
     * @return
     * */
    @GetMapping("/singleSource")
    public String searchSingleSource(@RequestParam("keywords")String keywords,@RequestParam("type")String type){

        List<Map<String,Object>>result=new ArrayList<>();
        String msg="";
        String status=Status.SUCCESS;
        boolean haskey=redisService.exists(type.concat("Key").concat(keywords));//判断缓存中是否存在
        if(haskey){
            result= (List<Map<String, Object>>)(redisService.get(type.concat("Key").concat(keywords)));
            msg="数据来自redis缓存";
        }else {
            result=commonDao.selectSourceLike(keywords,"name",type);
            if(result==null||result.isEmpty()){
                msg=Context.NoSuchFile;
                status=Status.FAIL;
            }else {
                msg="数据来自直接在数据库中查询";
                redisService.set(type.concat("Key").concat(keywords),result,10L,TimeUnit.MINUTES);
            }

        }
        return ResultUtil.buildJSONResult(status,msg,result);
    }


    /**
     * 预先加载
     * */
    @GetMapping("/hotSource")
    public String hotSource(){

        String status=Status.SUCCESS;
        String msg=Context.Success;
        List<Object> hotAudios=null;
        boolean hasAudioKey=redisService.exists("hotAudio");
        if (hasAudioKey){
            hotAudios=redisService.lRange("hotAudio",0,10);
            //hotAudios=JSONObject.parseArray(JSONObject.toJSONString(redisService.get("hotAudio")),Audio.class);
        }else {
            //获取


            //写入reids
            String audios= JSON.toJSONString(hotAudios);
            redisService.set("hotAudio",audios,10L,TimeUnit.MINUTES);
        }

        boolean hasVideoKey=redisService.exists("hotVideo");
        List<Video>hotVideos=null;
        if(hasVideoKey){
            hotVideos=JSONObject.parseArray(JSONObject.toJSONString(redisService.get("hotVideo")),Video.class);
        }else {
            //获取


            //写入redis
             String video=JSON.toJSONString(hotVideos);
             redisService.set("hotVideo",video,10L,TimeUnit.MINUTES);
        }

        Map<String,Object>result=new HashMap<>();
        result.put("video",hotVideos);
        result.put("audio",hotAudios);


        return ResultUtil.buildJSONResult(status,msg,result);
    }

}
