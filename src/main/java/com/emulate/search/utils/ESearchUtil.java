package com.emulate.search.utils;

import com.emulate.search.common.Context;
import com.emulate.search.po.SearchProperty;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Map;
import static org.elasticsearch.index.query.QueryBuilders.*;


public class ESearchUtil {

    /**
     *简易通用es分页查询
     * @param clazz 资源类型
     * @param pageSize 每页数据数量
     * @param pageNo 页码
     * @param elasticsearchTemplate
     * @param searchProperty 查询的条件
     * */
    public <T>Page queryPagedSource(ElasticsearchTemplate elasticsearchTemplate,Integer pageNo, Integer pageSize, SearchProperty searchProperty, Class<T>clazz) {


        NativeSearchQueryBuilder sql=new NativeSearchQueryBuilder();
        if(StringUtils.isNotEmpty(searchProperty.getIndex())){
            sql.withIndices(searchProperty.getIndex());
        }

        if(StringUtils.isNotEmpty(searchProperty.getType())){
            sql.withTypes(searchProperty.getType());
        }

        //分页查询，1、按照某个字段排序，降序；2、默认排序
        Pageable pageable= null;
        if(StringUtils.isNotEmpty(searchProperty.getOrderByStr())){
            pageable= PageRequest.of(pageNo,pageSize, Sort.by(Sort.Direction.DESC,searchProperty.getOrderByStr()));
        }else {
            pageable=PageRequest.of(pageNo,pageSize);
        }
        sql.withPageable(pageable);

        BoolQueryBuilder queryBuilder=boolQuery();
        if (searchProperty.getFields()==null||searchProperty.getFields().length==0){
            //模糊匹配，默认为根据资源名字进行模糊匹配
            queryBuilder.filter(matchQuery(searchProperty.getKeywords(), Context.esDefaultFields));
        }else {
            //在多个字段中进行模糊匹配
            queryBuilder.filter(multiMatchQuery(searchProperty.getKeywords(),searchProperty.getFields()));
        }

        //多条件设置,支持or、and的选择
        if(searchProperty.getParam()!=null&&!searchProperty.getParam().isEmpty()){
            if(searchProperty.isOr()){
                Map<String,String>params=searchProperty.getParam();
                for (Map.Entry<String,String> entry:params.entrySet()){
                    queryBuilder.should(termQuery(entry.getKey(),entry.getValue()));
                }
            }else {
                Map<String,String>params=searchProperty.getParam();
                for (Map.Entry<String,String> entry:params.entrySet()){
                    queryBuilder.must(termQuery(entry.getKey(),entry.getValue()));
                }
            }
        }

        Page page = elasticsearchTemplate.queryForPage(sql.withQuery(queryBuilder).build(),clazz);

        return page;
    }


    public static void main(String []args){
        boolean s=false;
        System.out.println(s);
    }
}
