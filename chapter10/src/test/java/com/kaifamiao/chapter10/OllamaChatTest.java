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
package com.kaifamiao.chapter10;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class OllamaChatTest {
    @Test
    void testOllamaChat(@Autowired OllamaChatModel ollamaChatModel) {
        String message = "你好，请用300字以内介绍一下你自己";
//        Flux<ChatResponse> stream  = ollamaChatModel.stream(new Prompt(message));
//        stream.toIterable().forEach(response -> {
//            log.info("response: {}", response.getResult().getOutput().getText());
//        });
        ChatResponse response = ollamaChatModel.call(new Prompt(message));
        log.info("response: {}", response.getResult().getOutput().getText());
    }
}