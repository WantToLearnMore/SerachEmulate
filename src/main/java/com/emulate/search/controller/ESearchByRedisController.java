package com.emulate.search.controller;

import com.emulate.search.dao.elasticsearch.ElasticDao;
import com.emulate.search.po.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("ESearch")
public class ESearchByRedisController {

    @Autowired
    private ElasticDao elasticDao;

    @GetMapping("/search")
    public String search(@Valid RequestInfo requestInfo){



        return "";
    }

}
