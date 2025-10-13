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
package com.kaifamiao.chapter11;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@Slf4j
public class VectorStoreTest {

    @Test
    void loadDataToMilvus(@Autowired VectorStore vectorStore) throws IOException {
        log.info("vectorStore:{}", vectorStore.getClass());
        List<QaJson> list = loadData();
        for (QaJson qaJson : list) {
            log.info("qaJson->docs:{}", qaJson.docs());
            for (String doc : qaJson.docs()) {
                Document phoneDoc = Document.builder()
                        .text(doc) // 文本内容
                        .build();
                vectorStore.add(List.of(phoneDoc));
            }
        }

    }

    private List<QaJson> loadData() throws IOException {
        ObjectMapper      mapper   = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("notebook-qa.json");
        List<QaJson> list = mapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
        return list;
    }
}