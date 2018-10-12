package com.emulate.search.controller;


import com.emulate.search.po.RequestInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("ESearch")
public class ESearchByRedisController {

    @GetMapping("/search")
    public String search(RequestInfo requestInfo){



        return "";
    }

}
