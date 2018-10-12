package com.emulate.search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.Audio;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.po.Video;
import com.emulate.search.service.RedisService;
import com.emulate.search.service.SearchService;
import com.emulate.search.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @Autowired
    private SearchService searchService;


    /**
     * 所有包含关键字的视频或音频文件
     * @param requestInfo
     * @return
     * */
    @GetMapping("/search")
    public String searchSource(RequestInfo requestInfo){

        String msg="";
        List result=null;
        Long start=System.currentTimeMillis();
        List sourceInRedis=searchService.searchFromRedis(redisService,requestInfo);

        if(sourceInRedis==null||sourceInRedis.isEmpty()){
            result=searchService.searchWithRedis(redisService,requestInfo,commonDao);
            msg="本次查询的数据来自数据库！！！";
        }else {
            result=sourceInRedis;
            msg="本次查询的数据来自redis缓存！！！";;
        }
        String status=Status.SUCCESS;
        if(result==null||result.isEmpty()){
            status=Status.FAIL;
            msg=Context.NoSuchFile;
        }
        Long end=System.currentTimeMillis();
        return ResultUtil.buildJSONResult(status,msg,String.valueOf(end-start),result);
    }


    /**
     * 检索资源的详细信息
     * @param id 资源的id
     * @param type 目标资源类型，目前可选值：video，audio
     * @return
     * */
    @GetMapping("/singleSource")
    public String searchSingleSource(@RequestParam("id")int id,@RequestParam("type")String type){

        Long start=System.currentTimeMillis();
        Object result=null;
        String msg="";
        String status=Status.SUCCESS;
        boolean haskey=redisService.exists(type.concat("Key").concat(String.valueOf(id)));//判断缓存中是否存在
        if(haskey){
            result= redisService.get(type.concat("Key").concat(String.valueOf(id)));
            msg="数据来自redis缓存";
        }else {
            String soucrceKey=null;
            if(type.equals("video")){
                result=searchService.findDetails(commonDao,id,type, Video.class);
                soucrceKey=Context.videoKey;
            }else {
                result=searchService.findDetails(commonDao,id,type, Audio.class);
                soucrceKey=Context.audioKey;
            }
            if(result==null){
                msg=Context.NoSuchFile;
                status=Status.FAIL;
            }else {
                msg="数据来自直接在数据库中查询";
                redisService.set(soucrceKey.concat(String.valueOf(id)),result,10L,TimeUnit.MINUTES);
            }

        }
        Long end =System.currentTimeMillis();
        return ResultUtil.buildJSONResult(status,msg,String.valueOf(end-start),result);
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

        return ResultUtil.buildJSONResult(status,msg,"",result);
    }

}
