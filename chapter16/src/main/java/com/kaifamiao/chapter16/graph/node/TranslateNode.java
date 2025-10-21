package com.kaifamiao.chapter16.graph.node;

import com.alibaba.cloud.ai.graph.GraphResponse;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.streaming.FluxConverter;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
// 如果用户选择继续，就执行翻译。
public class TranslateNode implements NodeAction {

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            将以下问题翻译成 {targetLanguage}。
            如果已经是目标语言，请保持不变。
            不要添加任何解释。
            
            原始问题: {query}
            """);

    private final ChatClient chatClient;

    private final String TARGET_LANGUAGE = "English";

    public TranslateNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) {
        log.info("translate node is running.");
        // 从状态中获取要翻译的问题
        String query = state.value("query", "");
        // 从状态中获取目标语言,默认英语
        String targetLanguage = state.value("targetLanguage", TARGET_LANGUAGE);

        Flux<ChatResponse> chatResponseFlux = this.chatClient
                .prompt()
                .user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate())
                        .param("targetLanguage", targetLanguage)
                        .param("query", query))
                .stream()
                .chatResponse();
        Flux<GraphResponse<StreamingOutput>> generator = FluxConverter.builder()
                .startingNode("translate_llm_stream") //从上一个节点开始
                .startingState(state)
                .mapResult(response -> {
                    String       text          = response.getResult().getOutput().getText();
                    List<String> queryVariants = Arrays.asList(text.split("\n"));
                    return Map.of("translatecontent", queryVariants);
                }).build(chatResponseFlux);
        return Map.of("translatecontent", generator);
    }
}
