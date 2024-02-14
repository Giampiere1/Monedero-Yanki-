package com.nttdata.monedero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.nttdata.monedero.infraestructure.entities.WalletEntity;

@Configuration
public class RedisConfig {
  @Bean("walletReactiveRedisTemplate")
  public ReactiveRedisTemplate<String, WalletEntity> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {
    RedisSerializationContext<String, WalletEntity> serializationContext = RedisSerializationContext
        .<String, WalletEntity>newSerializationContext(new StringRedisSerializer())
        .key(new StringRedisSerializer())
        .value(new Jackson2JsonRedisSerializer<>(WalletEntity.class))
        .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
        .hashValue(new GenericJackson2JsonRedisSerializer())
        .build();
    return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
  }
}
