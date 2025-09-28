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
package com.kaifamiao.chapter02.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        // this.chatClient = builder.build();
        this.chatClient = builder
                .defaultSystem("你是一个精通Java的工程师，专门解决Java遇到的问题。")// 设定默认角色
                .defaultUser("你是谁？")//设置默认问题
                .build();
    }

    // http://localhost:8080/chat/sync?question=什么是SpringAI
    @GetMapping("/chat/sync")
    public String syncChat(@RequestParam(defaultValue = "推荐10本经典的Java书籍")
                           String question) {

        return chatClient.prompt().user(question).call().content();
    }

    // http://localhost:8080/chat/stream?question=什么是SpringAI
    @GetMapping(value = "/chat/stream", produces = "text/html;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam(defaultValue = "推荐10本经典的Java书籍")
                                   String question) {

        return chatClient.prompt()
                .user(question)
                .stream().content();
    }

    // http://localhost:8080/chat/prompt1
    @GetMapping(value = "/chat/prompt1", produces = "text/html;charset=UTF-8")
    public Flux<String> prompt1(@RequestParam(defaultValue = "推荐10本经典的Java书籍")
                                String question) {

        SystemMessage systemMessage = new SystemMessage("请按照以下格式输出：\n" +
                "1. 输出格式为：序号. 标题\n" +
                "2. 输出的序号为1-10，从1开始\n" +
                "3. 输出的标题为10个字以内的中文标题");
        UserMessage userMessage = new UserMessage(question);

        // 将消息封装成Prompt
        Prompt prompt = new Prompt(systemMessage, userMessage);

        return chatClient.prompt(prompt)
                .stream().content();
    }

    // http://localhost:8080/chat/prompt2
    @GetMapping(value = "/chat/prompt2", produces = "text/html;charset=UTF-8")
    public Flux<String> prompt2(@RequestParam(defaultValue = "推荐10本经典的Java书籍")
                                String question) {
        List<Message> messages = new ArrayList<>();
        //1. 首先设置系统指令，定义AI的行为（例如：作为一个友好的助手）
        SystemMessage systemMessage = new SystemMessage("你是一个友好的助手，熟悉Java相关的书记，乐于用中文帮助用户回答Java书籍相关问题。" +
                "以列表的形式输出");
        messages.add(systemMessage);

        //2. 第一轮对话：用户提问
        UserMessage userMessage1 = new UserMessage(question);
        messages.add(userMessage1);

        //3. 构建Prompt并发送请求
        Prompt prompt = new Prompt(messages);
        // 获取AI的回复
        String responseText = chatClient.prompt(prompt).call().content();
        log.info("第一轮对话输出:{}", responseText);

        // 将AI的回复（AssistantMessage）加入历史记录，以备下一轮使用
        messages.add(new AssistantMessage(responseText));

        // 4. 第二轮对话：用户基于上一轮的内容继续提问
        messages.add(new UserMessage("列表中第一个书名是什么？它主要讲解了哪些Java知识？"));

        //再次构建Prompt，此时messages中包含了完整的对话上下文
        prompt = new Prompt(messages);
        return chatClient.prompt(prompt)
                .stream().content();
    }

    // http://localhost:8080/chat/promptTpl
    @GetMapping(value = "/chat/promptTpl", produces = "text/html;charset=UTF-8")
    public Flux<String> promptTpl(@RequestParam(defaultValue = "请生成一个周报")
                                  String question) {
        // 创建一个PromptTemplate
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("classpath:/prompt/weekly-report.tpl");
        List<String>         acc                  = Arrays.asList("完成用户模块开发", "修复登录 Bug");
        List<String>         cha                  = Arrays.asList("第三方接口响应慢");
        // 设置占位符实际内容并返回system角色的message
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("projectName", "喵星球知识库",
                "accomplishments", String.join("\n", acc),
                "challenges", String.join("\n", cha)));
        // 设置用户的问题message
        Message userMessage = new UserMessage(question);
        // 组装Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt).stream().content();
    }
}