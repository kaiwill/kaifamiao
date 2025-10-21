package com.kaifamiao.chapter16.controller;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverEnum;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import com.kaifamiao.chapter16.controller.process.GraphProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/graph/human")
public class GraphHumanController {

    private static final Logger logger = LoggerFactory.getLogger(GraphHumanController.class);

    private final CompiledGraph compiledGraph;

    @Autowired
    public GraphHumanController(@Qualifier("humanGraph") StateGraph stateGraph) throws GraphStateException {
        // 关键配置：在 humanfeedback 节点前中断流程
        SaverConfig saverConfig = SaverConfig.builder()
                .register(SaverEnum.MEMORY.getValue(), new MemorySaver()).build();
        this.compiledGraph = stateGraph
                .compile(
                        CompileConfig.builder()
                                .saverConfig(saverConfig)
                                .interruptBefore("humanfeedback") //在 humanfeedback 前暂停
                                .build()
                );
    }

    @GetMapping(value = "/expand", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> expand(@RequestParam(value = "query") String query,
                                                @RequestParam(value = "threadId") String threadId) throws GraphRunnerException {
        RunnableConfig      runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        Map<String, Object> objectMap      = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expandernumber", 4);// 拆成4个问题

        GraphProcess                        graphProcess = new GraphProcess(this.compiledGraph);
        Sinks.Many<ServerSentEvent<String>> sink         = Sinks.many().unicast().onBackpressureBuffer();
        // 执行流程，流式返回结果
        Flux<NodeOutput> nodeOutputFlux = compiledGraph.fluxStream(objectMap, runnableConfig);
        // 将NodeOutput转为SSE发送
        graphProcess.processStream(nodeOutputFlux, sink);

        return sink.asFlux()
                .doOnCancel(() -> logger.info("Client disconnected from stream"))
                .doOnError(e -> logger.error("Error occurred during streaming", e));
    }

    // 恢复流程
    @GetMapping(value = "/resume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> resume(@RequestParam(value = "threadId") String threadId,
                                                @RequestParam(value = "feedback") boolean feedBack) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        // 获取当前暂停时的状态快照
        StateSnapshot stateSnapshot = this.compiledGraph.getState(runnableConfig);
        OverAllState  state         = stateSnapshot.state();
        state.withResume();

        Map<String, Object> objectMap = new HashMap<>();
        // 注入用户反馈
        objectMap.put("feedback", feedBack);
        state.withHumanFeedback(new OverAllState.HumanFeedback(objectMap, ""));

        // Create a unicast sink to emit ServerSentEvents
        Sinks.Many<ServerSentEvent<String>> sink         = Sinks.many().unicast().onBackpressureBuffer();
        GraphProcess                        graphProcess = new GraphProcess(this.compiledGraph);
        // 从当前状态恢复执行
        Flux<NodeOutput> resultFuture = compiledGraph.fluxStreamFromInitialNode(state, runnableConfig);
        // 将NodeOutput转为SSE发送
        graphProcess.processStream(resultFuture, sink);

        return sink.asFlux()
                .doOnCancel(() -> logger.info("Client disconnected from stream"))
                .doOnError(e -> logger.error("Error occurred during streaming", e));
    }
}
