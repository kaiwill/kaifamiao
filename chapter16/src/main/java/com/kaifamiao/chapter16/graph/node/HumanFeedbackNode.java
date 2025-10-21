package com.kaifamiao.chapter16.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

// 读取用户之前提供的反馈，决定下一步。
@Slf4j
public class HumanFeedbackNode implements NodeAction {

    @Override
    public Map<String, Object> apply(OverAllState state) {
        log.info("human_feedback node is running.");
        HashMap<String, Object> resultMap = new HashMap<>();
        String                  nextStep  = StateGraph.END;
        // 从状态中读取用户反馈
        Map<String, Object> feedBackData   = state.humanFeedback().data();
        boolean             shouldContinue = (boolean) feedBackData.getOrDefault("feed_back", true);
        // 如果执行下一步，则执行翻译
        if (shouldContinue) {
            nextStep = "translate";
        }
        // 将决策结果存入状态
        resultMap.put("humannextnode", nextStep);
        log.info("humannextnode node -> {} node", nextStep);
        return resultMap;
    }
}
