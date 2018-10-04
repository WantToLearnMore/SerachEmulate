package com.emulate.search.dao.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * redis队列工具
 */
public class QueueUtils {
    //左出
    public static String poll(StringRedisTemplate stringRedisTemplate, String... queueName){
        return poll(stringRedisTemplate, 5, queueName);
    }

    //左出
    public static String poll(StringRedisTemplate stringRedisTemplate, int seconds, String... queueName){

        Jedis jedis = null;
        RedisConnection connection = null;
        try {
            connection = stringRedisTemplate.getConnectionFactory().getConnection();
            jedis = (Jedis) (connection.getNativeConnection());
            String[] args = new String[queueName.length+1];
            for(int i=0;i<queueName.length;i++){
                args[i] = queueName[i];
            }
            args[queueName.length] = ""+seconds;
            List<String> blpopResult = jedis.blpop(args);
            if(blpopResult!=null && blpopResult.size()==2){
                return blpopResult.get(1);
            }
        }finally {
            if(connection != null){
                connection.close();
            }
        }

        return null;
    }

    //右入
    public static void push(StringRedisTemplate stringRedisTemplate, String queueName, Object value){
        stringRedisTemplate.opsForList().rightPush(queueName, JSON.toJSONString(value));
    }
}
