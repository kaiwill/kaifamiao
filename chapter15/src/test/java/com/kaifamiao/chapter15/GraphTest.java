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
package com.kaifamiao.chapter15;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = Application.class)
@Slf4j
public class GraphTest {
    @Test
    public void testSimpleGraph(@Autowired StateGraph stateGraph) throws GraphStateException {
        // 将图编译成CompiledGraph
        CompiledGraph compiledGraph = stateGraph.compile();

        String  threadId       = Thread.currentThread().threadId() + "";
        String  query          = "你好，很高兴认识你，能简单介绍一下自己吗？";
        Integer expanderNumber = 4;// 扩展为4个问题
        // 构建会话配置
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        // 入参配置
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expandernumber", expanderNumber);

        // 调用图
        Optional<OverAllState> invoke = compiledGraph.call(objectMap, runnableConfig);

        // 打印结果
        log.info("invoke: {}", invoke.map(OverAllState::data).orElse(new HashMap<>()));
    }
}