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
package com.kaifamiao.extra01;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class MemoryController {
    private final ChatClient chatClient;

    @Autowired
    public MemoryController(ChatClient.Builder chatClientBuilder
            , ChatMemory chatMemory) {

        // 通过不同角色Message方式传递聊天记忆
        Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        // 通过提示词的方式传递聊天记忆
        //Advisor promptChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        this.chatClient = chatClientBuilder
                // 通过Advisors设置聊天记忆
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
    }

    /**
     * @param question       问题
     * @param conversationId 聊天记忆的id
     */
    // http://localhost:8080/chat/memory?question=我是小明，为我推荐10部周星驰的电影&conversationId=1001
    @GetMapping(value = "/chat/memory", produces = "text/html;charset=UTF-8")
    public Flux<String> memory(@RequestParam(value = "question", required = true) String question
            , @RequestParam(value = "conversationId", required = true) Integer conversationId) {
        return this.chatClient.prompt()
                .user(question)
                // conversationId 很重要,多次提问时,请确保conversationId一致。这与 WEB应用中的session概念一致。
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

}