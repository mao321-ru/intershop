package org.example.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class RedisTest extends IntegrationTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void redisTemplate_checkTtl() throws InterruptedException {
        final String key = "test:redisTemplate_checkTtl";
        final String value = "v15";

        // Сохраняем запись в Redis с TTL = 1 секунда
        redisTemplate.opsForValue().set( key, value, 1, TimeUnit.SECONDS);

        // Проверяем наличие записи в Redis
        assertThat( redisTemplate.opsForValue().get( key))
            .withFailMessage("Value not found")
            .isNotNull()
            .isEqualTo( value);

        // Ждём, пока TTL истечёт
        TimeUnit.SECONDS.sleep(2L);

        // Проверяем, что запись пропала
        assertThat( redisTemplate.opsForValue().get( key))
                .withFailMessage("Value not expired")
                .isNull();
    }

}
