package com.emulate.search.controller;

import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.service.SearchService;
import com.emulate.search.utils.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchByNoRedisController {

    @Resource
    private CommonDao commonDao;

    /**
     * 搜索全部类容：视/音频
     * @param keywords 关键字
     * @return 符合关键字的相关视/音频
     * */
    @GetMapping("/searchSource")
    public String serach(@RequestParam("keywords")String keywords){

        List<Map<String,Object>>videos=commonDao.selectSourceLike(keywords,"name","video");
        List<Map<String,Object>>audios=commonDao.selectSourceLike(keywords,"name","audio");

        boolean hasSource=false;
        Map<String,Object>result=new HashMap<>();
        if(!(videos.isEmpty()||videos==null)){
            result.put("video",videos);
            hasSource=true;
        }
        if (!(audios==null||audios.isEmpty())){
            result.put("audio",audios);
            hasSource=true;
        }
        String status=Status.FAIL;
        String msg=Context.NoSuchFile;

        if (hasSource){
            status=Status.SUCCESS;
            msg=Context.Success;
        }
        return ResultUtil.buildJSONResult(status,msg,result);
    }

    /**
     * 搜索音频
     * @param keywords 搜索的关键字
     * @return 符合关键字的相关音频
     * */
    @GetMapping("/searchAudio")
    public String searchAudio(@RequestParam("keywords")String keywords){

        List<Map<String,Object>> audios=commonDao.selectSourceLike(keywords,"name","audio");
        String status=Status.SUCCESS;
        String msg=Context.Success;
        if(audios==null||audios.size()==0){
            status= Status.FAIL;
            msg=Context.NoSuchFile;
        }
         String result= ResultUtil.buildJSONResult(status,msg,audios);
        return result;
    }

    /**
     * 搜索视频
     * @param keywords 搜索的关键字
     * @return 符合关键字的相关视频
     * */
    @GetMapping("/searchVideo")
    public String searchVideo(@RequestParam("keywords")String keywords){

        List<Map<String,Object>>videos=commonDao.selectSourceLike(keywords,"name","video");

        String status=Status.SUCCESS;
        String msg= Context.Success;
        if(videos.size()==0||videos==null){
            status= Status.FAIL;
            msg=Context.NoSuchFile;
        }
        return ResultUtil.buildJSONResult(status,msg,videos);
    }
}
