package com.emulate.search.controller;


import com.emulate.search.common.Context;
import com.emulate.search.common.Status;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.service.ESearchService;
import com.emulate.search.utils.ESearchUtil;
import com.emulate.search.utils.ResultUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("ESearch")
public class ESearchController {

    @Autowired
    private ESearchService searchService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @GetMapping("/search")
    public String search(RequestInfo requestInfo){

        Long start=System.currentTimeMillis();
        Page result=searchService.searchResource(requestInfo);
        Long end=System.currentTimeMillis();
        String status= Status.SUCCESS;
        String msg= Context.Success;
        if(result==null||!result.hasContent()){
            status=Status.FAIL;
            msg=Context.NoSuchFile;
        }
        return ResultUtil.buildJSONResult(status,msg,String.valueOf(end-start),result);
    }

    @GetMapping("/test")
    public String test(){
        Long start=System.currentTimeMillis();
        List reuslt=ESearchUtil.testQ(elasticsearchTemplate);
        Long end=System.currentTimeMillis();
        return  ResultUtil.buildJSONResult("","",String.valueOf(end-start),reuslt );
    }

}
