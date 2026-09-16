package com.roushankumar.offlinepayment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisConnectionTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Test
  void shouldConnectToRedis() {
    redisTemplate.opsForValue().set("connection-test", "ok");

    assertThat(redisTemplate.opsForValue().get("connection-test")).isEqualTo("ok");
  }
}
