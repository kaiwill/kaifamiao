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
package com.kaifamiao.chapter08;

import com.kaifamiao.chapter08.service.MyEmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class EmbeddingModelTest {
    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private MyEmbeddingService myEmbeddingService;

    @Test
    public void testEmbeddingModel() {

        log.info("embeddingModel:{}", embeddingModel.getClass());//class com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel
        String  text      = "Hello, World!";
        float[] embedding = embeddingModel.embed(text);

        log.info("Embedding length:{} ", embedding.length);//1024

        for (int i = 0; i < 10; i++) {
            log.info("{} : {}", i, embedding[i]);
            /*
            0 : -0.040509745
            1 : 0.048558544
            2 : -0.06773139
            3 : -0.020919278
            4 : -0.06309953
            5 : -0.056987002
            6 : -0.033808745
            7 : -0.015091495
            8 : -0.020539619
            9 : 9.4796414E-4
             */
        }
    }

    @Test
    public void callTest() {
        var input = "你好";
        EmbeddingOptions embeddingOptions = EmbeddingOptionsBuilder.builder()
                // 设定embedding模型名称
                .withModel("text-embedding-v4")//该模型默认维度为1024
                .withDimensions(128) //设定embedding模型维度
                .build();
        EmbeddingRequest  embeddingRequest = new EmbeddingRequest(List.of(input), embeddingOptions);
        EmbeddingResponse response         = embeddingModel.call(embeddingRequest);
        log.info("EmbeddingResponseMetadata:{}", response.getMetadata().getUsage());
        response.getResults().forEach(result -> {
            log.info("EmbeddingResult:{}-> {}", result.getIndex(), result.getOutput());
        });
    }

    @Test
    public void cosineSimilarityTest() {
        // 1. 文本嵌入示例
        String        text1      = "Spring AI 是一个用于构建 AI 应用的框架";
        String        text2      = "Spring AI 帮助开发者快速集成人工智能功能";
        String        text3      = "Java 是一种跨平台的编程语言";
        List<float[]> embeddings = myEmbeddingService.embed(List.of(text1, text2, text3));
        log.info("向量维度:{} ", embeddings.getFirst().length);

        // 2. 计算余弦相似度
        double similarity1_2 = myEmbeddingService.cosineSimilarity(embeddings.get(0), embeddings.get(1));
        double similarity1_3 = myEmbeddingService.cosineSimilarity(embeddings.get(0), embeddings.get(2));
        System.out.printf("text1 与 text2 相似度: %.4f%n", similarity1_2); // 应接近 1
        System.out.printf("text1 与 text3 相似度: %.4f%n", similarity1_3); // 应较低
    }
}