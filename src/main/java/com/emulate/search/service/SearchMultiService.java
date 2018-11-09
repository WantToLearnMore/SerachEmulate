package com.emulate.search.service;

import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.RequestInfo;

import java.util.List;
import java.util.Map;

public interface SearchMultiService {
    public List<Map<String,Object>> multilSearchAdapter(RequestInfo requestInfo, CommonDao commonDao);
}
