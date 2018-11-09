package com.emulate.search.utils;

import com.emulate.search.common.Context;
import com.emulate.search.po.SearchProperty;
import com.emulate.search.po.elasticPO.Video;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;


public class ESearchUtil {

    private final static Logger logger =  LoggerFactory.getLogger(ESearchUtil.class);
    /**
     *简易通用es分页查询
     * @param clazz 资源类型
     * @param pageSize 每页数据数量
     * @param pageNo 页码
     * @param elasticsearchTemplate
     * @param searchProperty 查询的条件
     * */
    public static <T>Page queryPagedSource(ElasticsearchTemplate elasticsearchTemplate,Integer pageNo, Integer pageSize, SearchProperty searchProperty, Class<T>clazz) {

        NativeSearchQueryBuilder sql=new NativeSearchQueryBuilder();
        if(StringUtils.isNotEmpty(searchProperty.getIndex())){
            //类似设置数据库
            sql.withIndices(searchProperty.getIndex());
            logger.info("索引名："+searchProperty.getIndex());
        }

        if(StringUtils.isNotEmpty(searchProperty.getType())){
            //类似设置数据库表名
            sql.withTypes(searchProperty.getType());
            logger.info("类型名："+searchProperty.getType());
        }

        if (searchProperty.getFields()!=null||searchProperty.getFields().length!=0){
            //设置返回的字段
            sql.withFields(searchProperty.getFields());

            logger.info("设置属性");
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
        //多条件设置,支持or、and的选择
        if(searchProperty.getParam()==null||searchProperty.getParam().isEmpty()){
            return null;
        }else {
            logger.info("and 或者 or："+searchProperty.isOr());
            if(searchProperty.isOr()){
                Map<String,String>params=searchProperty.getParam();
                for (Map.Entry<String,String> entry:params.entrySet()){
                    queryBuilder.should(matchPhraseQuery(entry.getKey(),entry.getValue()));
                }
            }else {
                Map<String,String>params=searchProperty.getParam();
                for (Map.Entry<String,String> entry:params.entrySet()){
                    queryBuilder.must(matchPhraseQuery(entry.getKey(),entry.getValue()));
                }
            }
        }

        sql.withQuery(queryBuilder);
        Page result=null;
        try {
            logger.info(queryBuilder.toString());
            result = elasticsearchTemplate.queryForPage(sql.build(),clazz);
            logger.info(String.valueOf(result.getTotalElements()));
        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

    public static List findAllInfo(ElasticsearchTemplate elasticsearchTemplate){
        NativeSearchQueryBuilder sql=new NativeSearchQueryBuilder();
        sql.withIndices("searchemulate");
        sql.withTypes("video");
        sql.withFields(Context.searchFileds);
        sql.withFilter(matchPhraseQuery("name","战狼"));
        return elasticsearchTemplate.queryForList(sql.build(), Video.class);
    }

    public static List testQ(ElasticsearchTemplate elasticsearchTemplate){
        NativeSearchQueryBuilder sql=new NativeSearchQueryBuilder();
        sql.withIndices("searchemulate");
        sql.withTypes("video");
        sql.withFields(Context.searchFileds);

        BoolQueryBuilder queryBuilder=boolQuery();
        queryBuilder.filter(matchPhraseQuery("author","吴京"));
        sql.withQuery(queryBuilder);
        return elasticsearchTemplate.queryForList(sql.build(), Video.class);
    }
    public static void DInfo(ElasticsearchTemplate elasticsearchTemplate){
        NativeSearchQueryBuilder sql=new NativeSearchQueryBuilder();

    }
    public static void main(String []args){
        boolean s=false;
        System.out.println(s);
    }
}
