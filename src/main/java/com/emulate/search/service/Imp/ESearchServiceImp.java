package com.emulate.search.service.Imp;

import com.emulate.search.service.ESearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ESearchServiceImp implements ESearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List findResource(){


        return null;
    }

}
