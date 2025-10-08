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
package com.kaifamiao.chapter09;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;

@SpringBootTest
@Slf4j
public class MultimodalTest {
    @Test
    public void testMultimodal(@Autowired DashScopeChatModel dashScopeChatModel) throws IOException {

        Resource imageResource = new ClassPathResource("/multimodal.png");
        Media imageMedia = Media.builder()
                .mimeType(MimeTypeUtils.IMAGE_PNG)
                .data(imageResource.getContentAsByteArray())
                .build();
        // 创建一个 DashScopeMultimodalOptions 对象，并设置参数
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withMultiModel(true) // 启用多模态
                .withModel("qwen3-vl-plus")
                .build();

        // 创建一个 UserMessage 对象，并设置参数
        Message userMessage = UserMessage.builder()
                .media(imageMedia)
                .text("请用中文描述图片内容").build();
        // 创建一个 Prompt 对象，并设置
        Prompt prompt = Prompt.builder()
                .chatOptions(options)
                .messages(userMessage)
                .build();
        var response = dashScopeChatModel.call(prompt);
        log.info("response: {}", response.getResult().getOutput().getText());

    }
}