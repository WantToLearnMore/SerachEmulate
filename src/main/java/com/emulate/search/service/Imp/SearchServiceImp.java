package com.emulate.search.service.Imp;

import com.emulate.search.common.Context;
import com.emulate.search.controller.UserController;
import com.emulate.search.dao.common.VideoDao;
import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.po.RequestInfo;
import com.emulate.search.po.Video;
import com.emulate.search.service.RedisService;
import com.emulate.search.service.SearchService;
import com.emulate.search.utils.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SearchServiceImp implements SearchService {

    private final static Logger logger =  LoggerFactory.getLogger(SearchServiceImp.class);
    @Autowired
    private VideoDao videoDao;

    @Override
    public <T>T findDetails(CommonDao commonDao,int id,String type,Class<T>T){
        return commonDao.selectById(id,type,T);
    }

    /**
     * 在redis中查找数据
     * @param requestInfo
     * @param redisService
     */
    @Override
    public List searchFromRedis(RedisService redisService,RequestInfo requestInfo){
        Long start=System.currentTimeMillis();
        Map<String,Object> result=new HashMap<>();
        List<String>authorIds=null;
        List<String>nameIds=null;
        List<Map<String,Object>>names=null;
        List<Map<String,Object>>authors=null;
        boolean hasName=false;
        boolean hasAuhor=false;
        if(StringUtils.isNotEmpty(requestInfo.getKeywords())){
            logger.info("读取是的key："+requestInfo.getSourceType().concat("Name").concat(requestInfo.getKeywords()));
            hasName=redisService.exists(requestInfo.getSourceType().concat("Name").concat(requestInfo.getKeywords()));
            System.out.println(hasName);
        }
       if(StringUtils.isNotEmpty(requestInfo.getAuthor())){
           logger.info("读取是的key："+requestInfo.getSourceType().concat("Author").concat(requestInfo.getAuthor()));
           hasAuhor=redisService.exists(requestInfo.getSourceType().concat("Author").concat(requestInfo.getAuthor()));
           System.out.println(hasAuhor);
       }

        if(hasName==false&&hasAuhor==false){

            logger.info("进来了");
            return null;
        }
        if(StringUtils.isNotEmpty(requestInfo.getKeywords())){
            if (requestInfo.getSourceType()=="video"){
                nameIds=findSourceKey(redisService,Context.videoName,requestInfo.getKeywords());
                System.out.println(nameIds);
                if(nameIds!=null&&!nameIds.isEmpty()){
                    authors=findSource(nameIds,Context.videoKey,redisService);
                }
            }else {
                nameIds=findSourceKey(redisService,Context.audioName,requestInfo.getKeywords());
                if(nameIds!=null&&!nameIds.isEmpty()){
                    authors=findSource(nameIds,Context.audioName,redisService);
                }

            }
        }

        if(StringUtils.isNotEmpty(requestInfo.getAuthor())){
            if (requestInfo.getSourceType()=="video"){
                authorIds=findSourceKey(redisService, Context.videoAuthor,requestInfo.getAuthor());
                if(authorIds!=null&&!authorIds.isEmpty()){
                    authors=findSource(authorIds,Context.videoKey,redisService);
                }

            }else {
                authorIds=findSourceKey(redisService, Context.audioAuthor,requestInfo.getAuthor());
                if(authorIds!=null&&!authorIds.isEmpty()){
                    authors=findSource(authorIds,Context.audioKey,redisService);
                }

            }
        }

        //1-or;2-and
        if(requestInfo.getOperition()==1){
            if(authors!=null&&!authors.isEmpty()){
                //去重
                for(Map<String,Object> map:authors){
                    result.put(String.valueOf(map.get("id")),map);
                }
            }

            if(names!=null&&!names.isEmpty()){
                for(Map<String,Object>map:names){
                    result.put(String.valueOf(map.get("id")),map);
                }
            }

        }else {
            if(authors!=null&&!authors.isEmpty()){
                for(Map<String,Object>map:authors){
                    if(map.get("name")==requestInfo.getKeywords())
                        result.put(String.valueOf(map.get("id")),map);
                }
            }

            if(names!=null&&!names.isEmpty()){
                for(Map<String,Object>map:names){
                    if(map.get("author")==requestInfo.getAuthor())
                        result.put(String.valueOf(map.get("id")),map);
                }
            }
        }
        if(result!=null&&!result.isEmpty()){
            logger.info("searchFormRedis"+String.valueOf(System.currentTimeMillis()-start));
            return new ArrayList(result.values());
        }
        logger.info("searchFormRedis"+String.valueOf(System.currentTimeMillis()-start));
        return null;
    }

    @Override
    public List<Map<String,Object>>searchSource(CommonDao commonDao, RequestInfo requestInfo){
        List<Map<String,Object>>result=null;
        if(StringUtils.isNotEmpty(requestInfo.getSourceType())){
            if(StringUtils.isNotEmpty(requestInfo.getAuthor())&&StringUtils.isNotEmpty(requestInfo.getKeywords())){
                result=commonDao.selectSourceLike(requestInfo.getAuthor(),requestInfo.getKeywords(),"author","name",requestInfo.getOperition(),requestInfo.getSourceType(),Context.searchFileds);
            }else if (StringUtils.isNotEmpty(requestInfo.getKeywords())){
                 result=commonDao.selectSourceLike(requestInfo.getKeywords(),"name",requestInfo.getSourceType(),Context.searchFileds);
                }
                else if(StringUtils.isNotEmpty(requestInfo.getAuthor())){
                result=commonDao.selectSourceLike(requestInfo.getAuthor(),"author",requestInfo.getSourceType(),Context.searchFileds);
                }
            }
        return result;
    }

    /**
     * 从数据库中搜索数据，并写入redis
     * @param requestInfo
     * @param commonDao
     * @param redisService
     * */
    @Override
    public List<Map<String,Object>> searchWithRedis(RedisService redisService,RequestInfo requestInfo,CommonDao commonDao){
       Long start=System.currentTimeMillis();
        List<Map<String,Object>>result=searchSource(commonDao,requestInfo);//查询数据
       logger.info("查询用时："+String.valueOf(System.currentTimeMillis()-start));
        //写入redis。两步：1、fiield-ids  2、id-object
       if (result==null||result.isEmpty()){
           return null;
       }else {
           setSourceIdsInRedis(result,"name",redisService,requestInfo);
           setSourceIdsInRedis(result,"author",redisService,requestInfo);
           logger.info("储存ids："+String.valueOf(System.currentTimeMillis()-start));
           for (Map<String,Object>map:result) {
               redisService.set(getSourceKey(requestInfo,"id").concat(String.valueOf(map.get("id"))),map,10L,TimeUnit.MINUTES);
           }
           logger.info("储存对象："+String.valueOf(System.currentTimeMillis()-start));
       }
        return result;
    }
    @Override
    public Video saveVideo(Video video){
        return videoDao.save(video);
    }

    //在redis中查找资源
    private Map<String,Object> findFromRedis(RedisService redisService,String key,String searchKey){
        Long start= System.currentTimeMillis();
        boolean haskey=redisService.exists(key.concat(searchKey));
        if(haskey){
            logger.info("findFormRedis："+String.valueOf(System.currentTimeMillis()-start));
            return ObjectUtil.objectToMap(redisService.get(key.concat(searchKey)));
        }
        logger.info("findFormRedis："+String.valueOf(System.currentTimeMillis()-start));
        return null;
    }

    //检索关键字在redis中对应资源的ids
    private List<String> findSourceKey(RedisService redisService,String key,String searchKey){
        boolean haskey=redisService.exists(key.concat(searchKey));
        System.out.println("检索时的key："+key.concat(searchKey));
        if(haskey){
            System.out.println("ids:"+(List<String>) redisService.get(key.concat(searchKey)));
            return (List<String>) redisService.get(key.concat(searchKey));
        }
        return null;
    }

    //通过资源id号在redis中查询
    private List<Map<String,Object>>findSource(List<String>ids,String key,RedisService redisService){
       List<Map<String,Object>>result=new ArrayList<>();
        for(String s:ids){
            result.add(findFromRedis(redisService,key,s));
        }
        return result;
    }

    //设置source-id
    private void setSourceIdsInRedis(List<Map<String,Object>>source,String field,RedisService redisService,RequestInfo requestInfo){
        String sourceKey=getSourceKey(requestInfo,field);
        List<Map<String,Object>>tmpsource=new ArrayList<>(source);
         int size=tmpsource.size();
        int n=0;
        for (int i=0;i<size;i++) {

            Map<String,Object>map=tmpsource.get(i);
            List<String>ids=new ArrayList<>();
            ids.add(String.valueOf(map.get("id")));
            String str=String.valueOf(map.get(field));

            for(int j=i+1;j<size;j++){
                Map<String,Object>m=tmpsource.get(j);
                String temp=String.valueOf(m.get(field));
                if(str.equals(temp)){
                    ids.add(String.valueOf(m.get("id")));
                    i++;
                }
            }
            n++;
            logger.info("写入是的key："+sourceKey.concat(str));
            redisService.set(sourceKey.concat(str),ids,10L, TimeUnit.MINUTES);
        }
        System.out.println("执行了："+n+"次！");
    }

    /**
     * 匹配资源写入redis时的标识
     * @param requestInfo
     * @param field 写入时的键
     * */
    private String getSourceKey(RequestInfo requestInfo,String field){
        String sourceKey=null;
        if(requestInfo.getSourceType().equals("video")){
            if(field.equals("name")){
                sourceKey=Context.videoName;
            }else if(field.equals("author")){
                sourceKey=Context.videoAuthor;
            }else {
                sourceKey=Context.videoKey;
            }
        }else if(requestInfo.getSourceType().equals("audio")){
            if(field.equals("name")){
                sourceKey=Context.audioName;
            }else if(field.equals("author")){
                sourceKey=Context.audioAuthor;
            }else {
                sourceKey=Context.audioKey;
            }
        }else {
            return null;
        }
        return sourceKey;
    }
}
