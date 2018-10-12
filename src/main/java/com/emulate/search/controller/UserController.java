package com.emulate.search.controller;

import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.Video;
import com.emulate.search.service.SearchService;
import com.emulate.search.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private SearchService searchService;

    @GetMapping("/insertData")
    public String es(){

        Long start=System.currentTimeMillis();
        List<Video>videos=ResultUtil.ReadDataFromFile("D:\\data0\\thee.txt");
        for (Video v:videos){
            Video st=searchService.saveVideo(v);
        }
        Long end=System.currentTimeMillis();
        Long time=end-start;
        return ResultUtil.buildJSONResult(Status.SUCCESS, "数据成功插入",String.valueOf(time),videos);
    }

    @GetMapping("/insertTXData")
    public String esTX(){

        Long start=System.currentTimeMillis();
        List<Video>videos=ResultUtil.ReadDataFromFileTX("D:\\data0\\txMovie.txt");
        for (Video v:videos){
            Video st=searchService.saveVideo(v);
            System.out.println("写入数据号："+st.getId());
        }
        Long end=System.currentTimeMillis();
        Long time=end-start;
        return ResultUtil.buildJSONResult(Status.SUCCESS, "数据成功插入",String.valueOf(time),videos);
    }

    @GetMapping("/delete")
    public String delete(){
        commonDao.delete(453);
        return "";
    }
}
