package com.emulate.search.service.Imp;

import com.emulate.search.common.Context;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.po.SearchProperty;
import com.emulate.search.po.elasticPO.Audio;
import com.emulate.search.po.elasticPO.Video;
import com.emulate.search.service.ESearchService;

import com.emulate.search.utils.ESearchUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ESearchServiceImp implements ESearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public <T>Page searchResource(RequestInfo requestInfo){

        SearchProperty searchProperty=new SearchProperty();
        searchProperty.setFields(Context.searchFileds);
        /*if(requestInfo.getSourceType().equals("video")){
            searchProperty.setIndex(Context.esVideoIndex);
        } else {
            searchProperty.setIndex(Context.esAudioIndex);
        }**/
        searchProperty.setIndex(Context.esDefaultIndex);
        searchProperty.setType(requestInfo.getSourceType());
        Map<String,String> param=new HashMap<>();
        if(StringUtils.isNotEmpty(requestInfo.getKeywords())){
            param.put("name",requestInfo.getKeywords());
        }
        if(StringUtils.isNotEmpty(requestInfo.getAuthor())){
            param.put("author",requestInfo.getAuthor());
        }
        searchProperty.setParam(param);
        Class clazz=null;
        if("video".equals(requestInfo.getSourceType())){
          clazz= Video.class;
        }else {
            clazz= Audio.class;
        }
        return ESearchUtil.queryPagedSource(elasticsearchTemplate,requestInfo.getPageNo(),requestInfo.getPageSize(),searchProperty,clazz);
    }

}
