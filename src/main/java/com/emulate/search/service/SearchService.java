package com.emulate.search.service;

import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.po.Video;

import java.util.List;
import java.util.Map;

public interface SearchService {
    public Video saveVideo(Video videos);
    public <T>T findDetails(CommonDao commonDao,int id,String type,Class<T>T);
    public List<Map<String,Object>>searchSource(CommonDao commonDao, RequestInfo requestInfo);
    public List searchFromRedis(RedisService redisService,RequestInfo requestInfo);
    public List<Map<String,Object>> searchWithRedis(RedisService redisService,RequestInfo requestInfo,CommonDao commonDao);
}
