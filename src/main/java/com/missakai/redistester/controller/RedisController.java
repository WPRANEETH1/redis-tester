package com.missakai.redistester.controller;

import com.missakai.redistester.dto.KeyValuePairDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RedisController {

    RedisTemplate<String, Object> redisTemplate;

    public RedisController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PutMapping
    public void setEx(@RequestBody KeyValuePairDto dto) {
        System.out.println(dto);
        redisTemplate.opsForValue().set(dto.getKey(), dto.getValue());
    }

    @GetMapping("/{key}")
    public Object get(@PathVariable String key) {
        System.out.println(key);
        System.out.println("******");
        System.out.println(redisTemplate.opsForValue().get(key));
        return redisTemplate.opsForValue().get(key);
    }

}
