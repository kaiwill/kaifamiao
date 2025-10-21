package com.kaifamiao.chapter16.graph.dispatcher;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

// 根据 HumanFeedbackNode 节点做出的决策，返回下一个应该执行的节点名称。
public class HumanFeedbackDispatcher implements EdgeAction {
    @Override
    public String apply(OverAllState state) throws Exception {
        // 从当前状态中读取 humannextnode 的值
        // 如果没有，就默认结束流程
        return (String) state.value("human_next_node", StateGraph.END);
    }
}
