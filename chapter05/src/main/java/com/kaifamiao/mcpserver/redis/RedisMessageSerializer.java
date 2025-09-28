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
package com.kaifamiao.mcpserver.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class RedisMessageSerializer implements RedisSerializer<Message> {
    private final ObjectMapper              objectMapper;
    private final JsonDeserializer<Message> messageDeserializer;

    public RedisMessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.messageDeserializer = new JsonDeserializer<>() {
            @Override
            public Message deserialize(JsonParser jp, DeserializationContext ctx)
                    throws IOException {
                ObjectNode root = jp.readValueAsTree();
                String     type = root.get("messageType").asText();

                return switch (type) {
                    case "USER" -> new UserMessage(root.get("text").asText());
                    case "ASSISTANT" -> new AssistantMessage(root.get("text").asText());
                    case "SYSTEM" -> new SystemMessage(root.get("text").asText());
                    default -> throw new UnsupportedOperationException("消息类型错误");
                };
            }
        };
    }

    @Override
    public byte[] serialize(Message message) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
    public Message deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return messageDeserializer.deserialize(objectMapper.getFactory().createParser(bytes), objectMapper.getDeserializationContext());
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }
}