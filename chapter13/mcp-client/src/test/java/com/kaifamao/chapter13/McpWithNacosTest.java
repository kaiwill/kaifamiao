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
package com.kaifamao.chapter13;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class McpWithNacosTest {

    @Test
  public  void testMcpWithNacos(@Autowired ChatClient.Builder chatClientBuilder,
                          // 通过@Qualifier注入从Nacos动态发现的工具
                          @Qualifier("loadbalancedMcpAsyncToolCallbacks") ToolCallbackProvider tools
    ) {
        var chatClient = chatClientBuilder
                .defaultToolCallbacks(tools.getToolCallbacks())
                .build();
        String result = chatClient.prompt("北京天气如何?")
                .call()
                .content();
        log.info("result:{}", result);
    }
}