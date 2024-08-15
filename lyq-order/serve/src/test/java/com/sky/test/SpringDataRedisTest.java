package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        System.out.println(redisTemplate);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        SetOperations setOperations = redisTemplate.opsForSet();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

    }

    /**
     * 操作字符串
     */
    @Test
    public void testString(){
        //set get setex setnx
        ValueOperations valueOperations = redisTemplate.opsForValue();

        redisTemplate.opsForValue().set("city","北京");
        System.out.println(redisTemplate.opsForValue().get("city"));

        redisTemplate.opsForValue().set("code","1234",3, TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfAbsent("lock","1");
    }

    /**
     * 操纵hash
     */

    @Test
    public void testhash(){
        //hset hget hdel hkeys hvals
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("key1","name","Tom");
        hashOperations.put("key1","age","20");

        String name  = (String) hashOperations.get("key1","name");
        System.out.println(name);

        Set key1 = hashOperations.keys("key1");
        System.out.println(key1);

        List values = hashOperations.values("key1");
        System.out.println(values);
    }








}
