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
package com.kaifamiao.chapter07;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class EmbeddingModelTest {
    @Autowired
    private EmbeddingModel embeddingModel;

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
}