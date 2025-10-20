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
package com.kaifamiao.chapter15.graph;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 自定义ExpanderNode节点：将用户的问题扩展成多角度的多个问题
public class ExpanderNode implements NodeAction {
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            您是信息检索和搜索优化方面的专家。
            您的任务是生成给定查询的 {number} 种不同版本。
            每个变体都必须涵盖该主题的不同视角或方面，同时保持原始查询的核心意图。其目的是扩大搜索范围，并提高找到相关信息的可能性。
            请勿解释您的选择或添加任何其他文字。
            请将查询变体以换行的方式分隔展示。
            原始查询：{query}
            查询变体：
            """);
    private final        ChatClient     chatClient;
    // 默认生成3个变体
    private final        Integer        NUMBER                  = 3;

    public ExpanderNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String  query          = state.value("query", "");
        Integer expanderNumber = state.value("expandernumber", this.NUMBER);

        // 调用大模型生成问题变体
        Flux<String> streamResult = this.chatClient.prompt()
                .user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate())
                        .param("number", expanderNumber)
                        .param("query", query))
                .stream().content();

        String result = streamResult.reduce("", (acc, item) -> acc + item).block();

        // 解析结果为列表
        List<String> queryVariants = Arrays.asList(result.split("\n"));

        // 返回结果
        Map<String, Object> resultMap = new HashMap<>();
        // 扩展的结果
        resultMap.put("expandercontent", queryVariants);
        return resultMap;
    }
}