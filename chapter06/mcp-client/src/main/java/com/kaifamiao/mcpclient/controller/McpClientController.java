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
package com.kaifamiao.mcpclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class McpClientController {
    private final ChatClient                  chatClient;
    private       SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

    // 注入ToolCallbackProvider
    @Autowired
    public McpClientController(ChatClient.Builder chatClientBuilder,
                               SyncMcpToolCallbackProvider syncMcpToolCallbackProvider
    ) {
        this.chatClient = chatClientBuilder
                // 设置默认的ToolCallback回调
                //.defaultToolCallbacks(syncMcpToolCallbackProvider)
                .build();
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
    }

    // http://localhost:8080/chat/mcpClient?question=请为我打开百度，并搜索关键字 Spring AI MCP
    @GetMapping(value = "/chat/mcpClient", produces = "text/html;charset=UTF-8")
    public Flux<String> mcpClient(@RequestParam(defaultValue = "请为我打开百度，并搜索关键字 Spring AI MCP")
                                  String question) {
        return this.chatClient.prompt()
                // 设置ToolCallback回调,如果没有设置则使用默认的
                .toolCallbacks(syncMcpToolCallbackProvider)
                .user(question)
                .stream()
                .content();
    }

    // http://localhost:8080/chat/sse?question=请帮我查询明天西安到北京所有列车车次
    @GetMapping(value = "/chat/sse")
    public String sse(@RequestParam(defaultValue = "请帮我查询明天西安到北京所有列车车次")
                      String question) {
        return this.chatClient.prompt()
                // 设置ToolCallback回调,如果没有设置则使用默认的
                .toolCallbacks(syncMcpToolCallbackProvider)
                .user(question)
                .call()
                .content();
    }

}