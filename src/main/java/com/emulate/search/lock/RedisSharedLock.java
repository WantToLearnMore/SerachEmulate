package com.emulate.search.lock;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;


public class RedisSharedLock {
    String lockName;
    long timeout = 1000;
    long waitTime = 100;
    long maxWaitTime = 30*1000;

    StringRedisTemplate stringRedisTemplate;

    public RedisSharedLock(String lockName, StringRedisTemplate stringRedisTemplate) {
        this.lockName = lockName;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void lock(){
        long startTime = System.currentTimeMillis();
        while (!tryLock()){
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(System.currentTimeMillis()-startTime>=maxWaitTime){
                throw new RuntimeException("get lock timeout.");
            }
        }

        Jedis jedis = null;
        RedisConnection connection = null;
        try {
            connection = stringRedisTemplate.getConnectionFactory().getConnection();
            jedis = (Jedis) (connection.getNativeConnection());
            jedis.set(lockName, "locked", "nx", "px", timeout);
        }finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    public boolean tryLock(){
        return !stringRedisTemplate.hasKey(lockName);
    }

    public void unlock(){
        stringRedisTemplate.delete(lockName);
    }
}
