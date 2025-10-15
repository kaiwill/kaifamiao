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
package com.kaifamiao.extra01.controller;

import com.kaifamiao.extra01.dto.ActorsFilms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ChatController {
    private final ChatClient chatClient;

    @Autowired
    private ChatModel chatModel;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("你是一个电影行业专家，专注于电影相关的问题。")// 设定默认角色
                .build();
    }

    // http://localhost:8080/chat/actor?actor=周星驰
    @GetMapping("/chat/actor")
    public ActorsFilms actor(@RequestParam(defaultValue = "刘德华")
                             String actor) {
        ActorsFilms actorsFilms = chatClient.prompt()
                // 输入提示词，用 {actor} 占位符传参数
                .user(u -> u.text("告诉我 {actor} 的 5 部电影")
                        .param("actor", actor))
                // 调用 AI 并转换为 ActorsFilms
                .call()//只能用同步的方式调用AI
                .entity(ActorsFilms.class);

        return actorsFilms;
    }

    // 低级API,理解底层原理
    // http://localhost:8080/chat/actor2?actor=周星驰
    @GetMapping("/chat/actor2")
    public ActorsFilms actor2(@RequestParam(defaultValue = "刘德华")
                              String actor) {
        // 1. 创建 BeanOutputConverter，指定目标类是 ActorsFilms
        BeanOutputConverter<ActorsFilms> converter = new BeanOutputConverter<>(ActorsFilms.class);

        // 2. 构建提示词：把“格式指令”（converter.getFormat()）加到提示词末尾
        String promptTemplate = """
                生成 {actor} 的 5 部电影
                {format}  // 这里会替换成转换器的格式指令
                """;
        // 替换占位符：actor 是参数，format 是转换器的格式指令

        Prompt prompt = new PromptTemplate(promptTemplate).create(
                Map.of("actor", actor, "format", converter.getFormat())
        );

        // 3. 调用 AI 并转换
        // 调用 AI 得到结果（generation 里包含 AI 返回的文本）
        Generation generation = chatModel.call(prompt).getResult();
        // 把 AI 输出的文本转成 ActorsFilms 对象
        ActorsFilms actorsFilms = converter.convert(generation.getOutput().getText());
        return actorsFilms;
    }

    // http://localhost:8080/chat/list?theme=科幻
    @GetMapping("/chat/list")
    public List<ActorsFilms> list(@RequestParam(defaultValue = "科幻")
                                  String theme) {
        // 1. 用 ParameterizedTypeReference 指定泛型类型
        ParameterizedTypeReference<List<ActorsFilms>> typeRef =
                new ParameterizedTypeReference<List<ActorsFilms>>() {
                };

        // 2. 高级 API 调用：生成科幻题材电影列表
        List<ActorsFilms> twoActorsFilms = chatClient.prompt()
                .user(u -> u.text("生成2位演员各自参演的 {theme}题材的5部电影,如果出现英文,请翻译为中文")
                        .param("theme", theme))
                .call()
                .entity(typeRef);
        return twoActorsFilms;
    }

    // http://localhost:8080/chat/map1
    @GetMapping("/chat/map1")
    public Map<String, Object> map1() {
        // 高级 API 实现
        Map<String, Object> numberMap = chatClient.prompt()
                .user("返回一个 Map，key 是 'numbers'，value 是 1-9数字的数组")
                .call()
                // 用 ParameterizedTypeReference 指定 Map 的泛型
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        return numberMap;
    }

    // http://localhost:8080/chat/map2
    @GetMapping("/chat/map2")
    public Map<String, Object> map2() {

        MapOutputConverter converter = new MapOutputConverter();
        String             format    = converter.getFormat(); // 格式指令：让 AI 返回 RFC8259 标准的 JSON
        log.info("格式指令：{}", format);

        String promptTemplate = "返回 key 是 'numbers'、value 是 1-9 数组的 Map\n{format}";

        Prompt prompt = new PromptTemplate(promptTemplate).create(Map.of("format", format));

        Generation generation = chatModel.call(prompt).getResult();
        log.info("AI 输出：{}", generation.getOutput().getText());

        Map<String, Object> resultMap = converter.convert(generation.getOutput().getText());
        return resultMap;
    }

    // http://localhost:8080/chat/listOutputConvert1
    @GetMapping("/chat/listOutputConvert1")
    public List<String> listOutputConvert1() {

        List<String> iceCreamFlavors = chatClient.prompt()
                .user(u -> u.text("列出 5 种 {subject}")
                        .param("subject", "冰淇淋口味"))
                .call()
                // 创建 ListOutputConverter，用默认的转换服务
                .entity(new ListOutputConverter(new DefaultConversionService()));
        return iceCreamFlavors;
    }

    // http://localhost:8080/chat/listOutputConvert2
    @GetMapping("/chat/listOutputConvert2")
    public List<String> listOutputConvert2() {
        ListOutputConverter converter = new ListOutputConverter(new DefaultConversionService());
        String              format    = converter.getFormat(); // 格式指令
        log.info("格式指令：{}", format);

        String promptTemplate = "列出 5 种冰淇淋口味\n{format}";

        Prompt prompt = new PromptTemplate(promptTemplate).create(Map.of("format", format));

        Generation generation = chatModel.call(prompt).getResult();
        log.info("AI 输出：{}", generation.getOutput().getText());

        List<String> iceCreamFlavors = converter.convert(generation.getOutput().getText());
        return iceCreamFlavors;
    }


}