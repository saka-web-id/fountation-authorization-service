package id.web.saka.fountation.authorization.config;


import com.fasterxml.jackson.databind.ObjectMapper;

import id.web.saka.fountation.authorization.company.role.permission.CompanyRolePermissionDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, CompanyRolePermissionDTO> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<CompanyRolePermissionDTO> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, CompanyRolePermissionDTO.class);

        RedisSerializationContext<String, CompanyRolePermissionDTO> context =
                RedisSerializationContext
                        .<String, CompanyRolePermissionDTO>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }







}
