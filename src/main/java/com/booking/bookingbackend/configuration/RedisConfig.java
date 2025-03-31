package com.booking.bookingbackend.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@Slf4j
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;


  /**
   * Creates and configures a JedisConnectionFactory to establish a connection
   * to the Redis data store using the specified host and port configuration.
   *
   * @return an instance of {@link JedisConnectionFactory} configured with the
   *         specified Redis standalone configuration.
   */
  //
  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    log.info("Connecting redis: {}:{}", redisHost, redisPort);
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(redisHost);
    config.setPort(redisPort);
    return new JedisConnectionFactory(config);
  }

  /**
   * Creates and configures a {@link RedisTemplate} bean for interacting with Redis.
   * The method sets up a Redis template with a connection factory built using the
   * {@code jedisConnectionFactory()} method and returns the configured template.
   *
   * @return an instance of {@link RedisTemplate} configured for string keys and object values.
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    log.info("Redis connected !");
    return template;
  }
}
