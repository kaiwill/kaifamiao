/*******************************************************************************
 * Copyright (c) 2010, 2025 西安秦晔信息科技有限公司
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.kaifamiao.chapter05.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaifamiao.chapter05.redis.RedisChatMemoryRepository;
import com.kaifamiao.chapter05.redis.RedisMessageSerializer;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ChatMemoryConfiguration {

    // 让ChatMemory 使用 JdbcChatMemoryRepository
//    @Bean
//    ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
//        return MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build();
//    }

    // 让ChatMemory 使用 RedisChatMemoryRepository
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build();
    }

    @Bean("chatMemoryRedisTemplate")
    @ConditionalOnMissingBean({RedisTemplate.class})
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(factory);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 配置序列化器
        RedisMessageSerializer redisMessageSerializer = new RedisMessageSerializer(om);
        StringRedisSerializer  stringRedisSerializer  = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(redisMessageSerializer);
        template.setHashValueSerializer(redisMessageSerializer);
        template.afterPropertiesSet();
        return template;
    }

}