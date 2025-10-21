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
public class ExpanderNode implements NodeAction {

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
            """
                    你是一个信息检索专家。
                    请生成 {number} 个与以下问题相关的不同版本。
                    每个版本应从不同角度提问，但保持原意。
                    只输出问题，每行一个，不要解释。
                    
                    原始问题: {query}
                    """
    );
    private final        ChatClient     chatClient;

    private final Integer DEFAULT_NUMBER = 3;

    public ExpanderNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) {
        log.info("expander node is running.");
        // 从状态中获取要扩展的问题
        String query = state.value("query", "");
        // 从状态中获取扩展后的问题数量
        Integer expanderNumber = state.value("expandernumber", this.DEFAULT_NUMBER);

        // 调用AI模型流式生成，获取扩展后的问题
        Flux<ChatResponse> chatResponseFlux = this.chatClient.prompt()
                .user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate())
                        .param("number", expanderNumber)
                        .param("query", query)
                ).stream().chatResponse();

        // 将流式结果转换成 GraphResponse<StreamingOutput>
        Flux<GraphResponse<StreamingOutput>> generator = FluxConverter.builder()
                .startingNode("expander_llm_stream")
                .startingState(state)
                .mapResult(response -> {
                    String       text          = response.getResult().getOutput().getText();
                    List<String> queryVariants = Arrays.asList(text.split("\n"));
                    return Map.of("expandercontent", queryVariants);
                }).build(chatResponseFlux);
        return Map.of("expandercontent", generator);
    }

}
