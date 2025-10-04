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
package com.kaifamiao.chapter08.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class RAGController {
    private final ChatClient chatClient;

    @Autowired
    public RAGController(ChatClient.Builder chatClientBuilder,
                         VectorStore vectorStore) {
        //通过Advisors方式，对向量数据库进行封装
//        Advisor questionAdvisor =QuestionAnswerAdvisor
//                .builder(vectorStore)
//                .build();
//        this.chatClient = chatClientBuilder
//                .defaultAdvisors(questionAdvisor)
//                .build();

        String promptTmp = """
                {query}
                参考信息如下，使用“---------------------”标识包裹在里面的为参考信息。
                ---------------------
                {question_answer_context}
                ---------------------
                鉴于当前的参考信息以及所提供的历史信息（而非任何先入为主的了解），请回复用户评论。如果答案不在上述背景信息中，告知用户您无法回答该问题。
                """;
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(promptTmp)
                .build();

        var b = new FilterExpressionBuilder().eq("source", "官方网站").build();
        QuestionAnswerAdvisor questionAdvisor =QuestionAnswerAdvisor
                .builder(vectorStore)
                .promptTemplate(promptTemplate)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.2) //相似度阈值，只有大于等于该值才会被返回（取值范围:0-1），默认是0（没有相似度排除）
                        .topK(2) //返回相似度排名前2的文档，默认是4
                        .filterExpression(b) // 通过文档的元数据过滤
                        .build())
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(questionAdvisor)
                .build();
    }

    @GetMapping(value="/chat/rag"
            , produces = "text/html;charset=UTF-8"
    )
    public String generate(@RequestParam(value = "question") String question) {
        return this.chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}