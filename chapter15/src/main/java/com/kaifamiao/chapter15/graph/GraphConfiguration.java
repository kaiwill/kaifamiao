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

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class GraphConfiguration {
    @Bean
    public StateGraph simpleGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        // 全局变量的替换策略（ReplaceStrategy为替换，AppendStrategy为追加）
        KeyStrategyFactory keyStrategyFactory = () -> {
            Map<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            // 用户输入
            keyStrategyHashMap.put("query", new ReplaceStrategy());
            keyStrategyHashMap.put("expandernumber", new ReplaceStrategy());
            keyStrategyHashMap.put("expandercontent", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        // 构造图
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                // 添加节点：问题扩展节点
                .addNode("expander", AsyncNodeAction.node_async(new ExpanderNode(chatClientBuilder)))
                // 添加边：START -> ExpanderNode
                .addEdge(StateGraph.START, "expander")
                // 添加边：ExpanderNode -> END
                .addEdge("expander", StateGraph.END);

        // 将图打印出来，可以使用 PlantUML 查看
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML, "expander flow");
        log.info("=== expander PlantUML  Flow ===");
        log.info(representation.content());
        log.info("==================================");
        return stateGraph;
    }
}