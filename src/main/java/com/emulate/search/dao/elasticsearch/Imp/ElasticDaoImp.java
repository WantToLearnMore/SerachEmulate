package com.emulate.search.dao.elasticsearch.Imp;

import com.emulate.search.dao.elasticsearch.ElasticDao;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ElasticDaoImp implements ElasticDao{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private int count(){
        return 0;
    }

    //检索某一资源的信息
    public <T>T  search(int id,Class<T>clazz){


        return null;
    }

    /**
     * 分页查询
     * @param index 库名
     * @param type  表名
     * @param keywords  关键字
     * @param pageNo  页码
     * @param pageSize  每页的大小
     * @param clazz  类型
     *
     */
    public <T>Page searchByName(String index,String type, String keywords,int pageNo,int pageSize,Class<T>clazz){

        Pageable pageable=new PageRequest(pageNo,pageSize);
        NativeSearchQueryBuilder searchQuery= new NativeSearchQueryBuilder().withQuery(matchQuery("name",keywords
        )).withIndices(index).withTypes(type).withPageable(pageable);

        return null;
    }


    public <T>Page queryPagedOrders(Integer pageNo, Integer pageSize, String name, Long minPrice, Long maxPrice,Class<T>clazz) {
        // 默认，价格升序（为了支持丰富的排序场景，建议将所有可能的排序规则放到统一的enum中
       // Pageable pageable = new PageRequest(pageNo, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "price")));

        //NativeSearchQueryBuilder nbq = new NativeSearchQueryBuilder().withIndices(OrderDocument.INDEX).withTypes(OrderDocument
                //.ORDER_TYPE).withSearchType(SearchType.DEFAULT).withPageable(pageable);


        BoolQueryBuilder bqb = boolQuery();
        // 匹配订单name
        if (StringUtils.isNotEmpty(name)) {
            bqb.must(termQuery("name", name));
        }
        // 查询价格区间 minPrice<=price<=maxPrice
        if (minPrice != null && minPrice >= 0) {
            bqb.filter(rangeQuery("price").gte(minPrice));
        }
        if (maxPrice != null && maxPrice >= 0) {
            bqb.filter(rangeQuery("price").lte(maxPrice));
        }

        //Page page = elasticsearchTemplate.queryForPage(nbq.withQuery(bqb).build(),clazz);

        return null;
    }

}
