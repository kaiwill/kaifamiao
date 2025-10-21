package com.kaifamiao.chapter16.config;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.kaifamiao.chapter16.graph.dispatcher.HumanFeedbackDispatcher;
import com.kaifamiao.chapter16.graph.node.ExpanderNode;
import com.kaifamiao.chapter16.graph.node.HumanFeedbackNode;
import com.kaifamiao.chapter16.graph.node.TranslateNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

@Configuration
public class GraphHumanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(GraphHumanConfiguration.class);

    @Bean
    public StateGraph humanGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        // 1. 定义状态的更新策略（简单替换）
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("query", new ReplaceStrategy())
                .addPatternStrategy("threadid", new ReplaceStrategy())
                .addPatternStrategy("expandernumber", new ReplaceStrategy())
                .addPatternStrategy("expandercontent", new ReplaceStrategy())
                .addPatternStrategy("feedback", new ReplaceStrategy())
                .addPatternStrategy("humannextnode", new ReplaceStrategy())
                .addPatternStrategy("translatelanguage", new ReplaceStrategy())
                .addPatternStrategy("translatecontent", new ReplaceStrategy())
                .build();
        // 2. 创建 StateGraph
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                // 添加三个节点
                .addNode("expander", node_async(new ExpanderNode(chatClientBuilder)))
                .addNode("translate", node_async(new TranslateNode(chatClientBuilder)))
                .addNode("humanfeedback", node_async(new HumanFeedbackNode()))
                // 定义固定连接
                .addEdge(StateGraph.START, "expander")
                .addEdge("expander", "humanfeedback")
                // 为 humanfeedback 节点添加“条件边”
                .addConditionalEdges("humanfeedback",
                        AsyncEdgeAction.edge_async((new HumanFeedbackDispatcher())), Map.of(
                                "translate", "translate",// 如果返回 "translate"，则跳转到 translate 节点
                                StateGraph.END, StateGraph.END) // 如果返回 END，则结束
                )
                .addEdge("translate", StateGraph.END);

        // 添加 PlantUML 打印
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "human flow");
        logger.info("=== expander UML Flow ===");
        logger.info(representation.content());
        logger.info("==================================");

        return stateGraph;
    }
}
