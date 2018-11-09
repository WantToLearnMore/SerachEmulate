package com.emulate.search.service;

import com.emulate.search.po.RequestInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ESearchService {

   /**
    * @param requestInfo 请求信息
    * */
    public <T>Page searchResource(RequestInfo requestInfo);
}
