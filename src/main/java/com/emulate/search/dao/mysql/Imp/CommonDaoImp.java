package com.emulate.search.dao.mysql.Imp;

import com.emulate.search.dao.mysql.CommonDao;
import com.emulate.search.utils.ObjectUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CommonDaoImp implements CommonDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据id号查询信息
     * @param id id号
     * @param tableName 表名
     * @param T 资源类型
     * @param fields 可变参数，属性名
     * @return 返回一条目标资源
     * */
    public <T>T selectById(int id,String tableName,Class<T>T,String ...fields){

        StringBuilder sql=new StringBuilder();
        sql.append("select ");
        sql.append(ObjectUtil.buildFieldString(fields));
        sql.append(" from ");
        sql.append(tableName);
        sql.append(" where id = ");
        sql.append(id);
        List<Map<String, Object>>result=jdbcTemplate.queryForList(sql.toString());
        return result.size()==0? null: ObjectUtil.mapToObject(result.get(0),T);
    }


    /**
     * 根据id号查询资源
     * @param id 资源在数据库中的id号
     * @param tableName 数据表的名字
     * @param T 资源类型
     * @param fields 需要查询的属性，以List封装
     * @return
     * */
    public <T>T selectById(int id,String tableName,Class<T>T,List<String>fields){
        String []field= (String[]) fields.toArray();
        StringBuilder sql=new StringBuilder();
        sql.append("select ");
        sql.append(ObjectUtil.buildFieldString(fields));
        sql.append(" from");
        sql.append(tableName);
        sql.append(" where id = ");
        sql.append(id);
        List<Map<String, Object>>result=jdbcTemplate.queryForList(sql.toString());
        return result.size()==0? null: ObjectUtil.mapToObject(result.get(0),T);
    }
    public List<Map<String,Object>> selectSourceLike(String key1,String key2,String scope1,String scope2,int option,String tableName,String ...fields){
        String op="or";
        if(option!=1){
            op="and";
        }
        StringBuilder sql=new StringBuilder("");
        sql.append(" select ");
        sql.append(ObjectUtil.buildFieldString(fields));
        sql.append(" from ");
        sql.append(tableName);
        sql.append(" where locate ('");
        sql.append(key1);
        sql.append("',");
        sql.append(scope1);
        sql.append(")");
        sql.append(" "+op+" ");
        sql.append(" locate ('");
        sql.append(key2);
        sql.append("',");
        sql.append(scope2);
        sql.append(")");

        List<Map<String,Object>>result=jdbcTemplate.queryForList(sql.toString());
        return result.size()==0?null:result;
    }

    /**
     * 模糊查询
     * @param key 关键字
     * @param scope 搜索的范围，即以那个字段为标准查询
     * @param tableName 表名
     * @param fields 结果需要的字段 使用可变参数，适用于字段少时
     * @return list 查询结果
     * */
    public List<Map<String,Object>> selectSourceLike(String key,String scope,String tableName,String ...fields){
        StringBuilder sql=new StringBuilder("");
          sql.append(" select ");
          sql.append(ObjectUtil.buildFieldString(fields));
          sql.append(" from ");
          sql.append(tableName);
          sql.append(" where locate ('");
          sql.append(key);
          sql.append("',");
          sql.append(scope);
          sql.append(")");
          List<Map<String,Object>>result=jdbcTemplate.queryForList(sql.toString());
        return result.size()==0?null:result;
    }

    /**
     * 模糊查询
     * @param key 关键字
     * @param scope 搜索的范围，即以那个字段为标准查询
     * @param tableName 表名
     * @param fields 结果需要的字段，适用于字段多时
     * @return list 查询结果
     * */
    public List<Map<String,Object>> selectSourceLike(String key,String scope,String tableName,List<String>fields){
        String []field= (String[]) fields.toArray();
        StringBuilder sql=new StringBuilder("");
        sql.append(" select ");
        sql.append(ObjectUtil.buildFieldString(fields));
        sql.append(" from ");
        sql.append(tableName);
        sql.append(" where locate ('");
        sql.append(key);
        sql.append("',");
        sql.append(scope);
        sql.append(")");
        List<Map<String,Object>>result=jdbcTemplate.queryForList(sql.toString());
        return result.size()==0?null:result;
    }



    public int countSource(String tableName){
        StringBuilder sql=new StringBuilder("");
        sql.append("select count(*) num from ");
        sql.append(tableName);
        Map<String,Object>map=jdbcTemplate.queryForMap(sql.toString());

        return Integer.valueOf(map.get("num").toString());
    }
    public void delete(int id){
        StringBuilder sql=new StringBuilder("");
        sql.append(" delete from video where id > ");
        sql.append(id);
        jdbcTemplate.execute(sql.toString());
    }

}
