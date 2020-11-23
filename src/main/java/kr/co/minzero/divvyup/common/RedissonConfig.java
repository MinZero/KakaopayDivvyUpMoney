package kr.co.minzero.divvyup.common;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.redis.port}")
    public int port;

    @Value("${spring.redis.host}")
    public String host;

    /**
     * 다중 서버의 병목 현상을 방지하기 위한 Redis 접속 클라이언트 설정
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port);
        return Redisson.create(config);
    }
}
