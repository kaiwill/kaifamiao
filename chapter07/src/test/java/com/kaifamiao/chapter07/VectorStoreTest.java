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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class VectorStoreTest {

    @Autowired
    private VectorStore vectorStore;

    @Test
    public void testVectorStore() {
        log.info("vectorStore:{}", vectorStore.getClass());//org.springframework.ai.vectorstore.SimpleVectorStore

        // 1. 创建文档元数据
        Map<String, Object> metadata = Map.of(
                "category", "电子产品",
                "price", 5999.00,
                "releaseDate", "2024-01-15",
                "image", "https://gw.alicdn.com/bao/uploaded/i2/O1CN01lNEzKr1QXBHH4WsQJ_!!4611686018427381953-0-rate.jpg_960x960.jpg_.webp"
        );
        // 2. 创建文档（指定ID、文本、元数据）
        Document phoneDoc = Document.builder()
                .id("doc-123")// 自定义ID
                .text("iPhone 12 配备A14芯片，6.1英寸屏幕，支持5G网络...") // 文本内容
                //.media(productImage) // 关联图片
                .metadata(metadata) // 元数据
                .build();
        // 3. 存入向量存储
        vectorStore.add(List.of(phoneDoc));

        // 4. 检索时获取带score的结果
        List<Document> results = vectorStore.similaritySearch("推荐一款支持5G的手机");
        Document       topDoc  = results.getFirst();
        log.info("匹配的文档ID：{}", topDoc.getId());
        log.info("匹配的文档元数据：{}", topDoc.getMetadata());
        log.info("匹配的文档文本：{}", topDoc.getText());
    }
}