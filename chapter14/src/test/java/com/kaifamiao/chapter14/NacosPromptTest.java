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
package com.kaifamiao.chapter14;

import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplate;
import com.alibaba.cloud.ai.prompt.ConfigurablePromptTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = Application.class)
@Slf4j
public class NacosPromptTest {
    @Test
    public void testNacosPromptTemplate(@Autowired ChatClient.Builder chatClientBuilder,
                                        @Autowired ConfigurablePromptTemplateFactory promptTemplateFactory
    ) {
        var chatClient = chatClientBuilder.build();
        //   1. 使用工厂创建一个名为 "author" 的可配置Prompt模板
        //    这里的 "author" 必须与Nacos配置中的 "name" 字段值完全匹配。
        ConfigurablePromptTemplate template = promptTemplateFactory.create(
                "author",// 模板名称 (template name)
                "请列出 {author} 的三本最著名的著作。" // 默认模板 (fallback template)
        );
        // 2. 将参数填入模板，生成最终的Prompt
        String              authorName = "鲁迅";
        Map<String, Object> parameters = Map.of("author", authorName);
        Prompt              prompt     = template.create(parameters);
        log.info("最终构建的 prompt 为：{}", prompt.getContents());


        String result = chatClient.prompt(prompt)
                .call()
                .content();
        log.info("result:{}", result);
    }
}