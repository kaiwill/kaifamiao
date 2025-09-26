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
package com.kaifamiao.chapter05.controller;

import com.kaifamiao.chapter05.service.WatcherToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * <p>ClassName: com.kaifamiao.chapter04.controller.ChatController
 * <p>Function: 大模型对话
 * <p>date: 2025-09-24 15:39
 *
 * @author wuqing
 * @version 1.0.0
 * @since JDK 17
 */
@RestController
@Slf4j
public class ChatController {
    private final ChatClient         chatClient;
    @Autowired
    private       WatcherToolService watcherToolService;

    public ChatController(ChatClient.Builder builder) {
        // this.chatClient = builder.build();
        this.chatClient = builder
                .build();
    }

    // http://localhost:8080/chat/watcher?question=今天西安天气如何?适合做什么活动?
    @GetMapping(value = "/chat/watcher", produces = "text/html;charset=UTF-8")
    public Flux<String> watcher(@RequestParam(defaultValue = "今天西安天气如何?适合做什么活动?")
                                String question) {

        return chatClient.prompt()
                .tools(watcherToolService)
                .user(question)
                .stream().content();
    }
}