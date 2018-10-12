package com.emulate.search.controller;

import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.Audio;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.po.Video;
import com.emulate.search.service.SearchService;
import com.emulate.search.utils.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchByNoRedisController {

    @Resource
    private CommonDao commonDao;

    @Autowired
    private SearchService searchService;

    /**
     * 搜索全部类容：视/音频
     * @param requestInfo
     * @return 符合关键字的相关视/音频
     * */
    @GetMapping("/searchSource")
    public String serach(RequestInfo requestInfo){

        Long start=System.currentTimeMillis();
        List<Map<String,Object>>result=searchService.searchSource(commonDao,requestInfo);
        Long end =System.currentTimeMillis();

        String status=Status.SUCCESS;
        String msg=Context.Success;
        if(result==null||result.isEmpty()){
            status=Status.FAIL;
            msg=Context.NoSuchFile;
        }

        return ResultUtil.buildJSONResult(status,msg,String.valueOf(end-start),result);
    }

    /**
     * 搜索具体某一资源的详情
     *@param type 资源类型
     * @param id  资源id
     * @return
     * */
    @GetMapping("/searchDetail")
    public String searchSingle(@RequestParam("id")int id,@RequestParam("type")String type){

        Long start =System.currentTimeMillis();
        Object result=null;
        if(type=="video"){
            result=searchService.findDetails(commonDao,id,type, Video.class);
        }else {
            result=searchService.findDetails(commonDao,id,type, Audio.class);
        }

        String status=Status.SUCCESS;
        String msg=Context.Success;
        if(result==null){
            status=Status.FAIL;
            msg=Context.NoSuchFile;
        }
        Long end =System.currentTimeMillis();
        return ResultUtil.buildJSONResult(status,msg,String.valueOf(end-start),result);
    }


}
