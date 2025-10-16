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
package com.kaifamiao.chapter12.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DefaultDynamicAiRouterService implements DynamicAiRouterService {

    private Map<String, ChatClient> chatClientMap = new HashMap<>();

    public DefaultDynamicAiRouterService(ObjectProvider<List<ChatModel>> chatModelListProvider) {
        List<ChatModel> chatModelList = chatModelListProvider.getIfAvailable();
        if (!CollectionUtils.isEmpty(chatModelList)) {
            for (ChatModel chatModel : chatModelList) {
                // 构建 ChatClient 对象
                ChatClient chatClient = ChatClient.builder(chatModel).build();
                if (chatModel instanceof ZhiPuAiChatModel) {
                    log.info("智谱AI模型");
                    chatClientMap.put(AiPlatform.ZHIPUAI.name(), chatClient);
                    continue;
                }
                if (chatModel instanceof DashScopeChatModel) {
                    log.info("百炼 DashScope");
                    chatClientMap.put(AiPlatform.DASHSCOPE.name(), chatClient);
                    continue;
                }
                if (chatModel instanceof OllamaChatModel) {
                    log.info("ollama");
                    chatClientMap.put(AiPlatform.OLLAMA.name(), chatClient);
                }
            }
        }
    }

    @Override
    public ChatClient chatClient(String aiPlatform) {
        if (chatClientMap.containsKey(aiPlatform)) {
            return chatClientMap.get(aiPlatform);
        }
        // 默认使用ollama
        return chatClientMap.get(AiPlatform.OLLAMA.name());
    }
}