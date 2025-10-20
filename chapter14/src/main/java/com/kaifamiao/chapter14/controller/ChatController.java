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
package com.kaifamiao.chapter14.controller;

import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplate;
import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@Slf4j
public class ChatController {
    private final ChatClient                        chatClient;
    private       ConfigurablePromptTemplateFactory promptTemplateFactory;


    public ChatController(ChatClient.Builder builder,
                          ConfigurablePromptTemplateFactory promptTemplateFactory) {
        this.chatClient = builder.build();
        this.promptTemplateFactory = promptTemplateFactory;
    }


    // http://localhost:8080/chat?author=鲁迅
    @GetMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam(name = "author", defaultValue = "鲁迅")
                                   String author) {
        //   1. 使用工厂创建一个名为 "author" 的可配置Prompt模板
        //    这里的 "author" 必须与Nacos配置中的 "name" 字段值完全匹配。
        ConfigurablePromptTemplate template = promptTemplateFactory.create(
                "author",// 模板名称 (template name)
                "请列出 {author} 的三本最著名的著作。" // 默认模板 (fallback template)
        );
        // 2. 将参数填入模板，生成最终的Prompt
        Map<String, Object> parameters = Map.of("author", author);
        Prompt              prompt     = template.create(parameters);
        log.info("最终构建的 prompt 为：{}", prompt.getContents());

        return chatClient.prompt()
                .user(author)
                .stream().content();
    }
}