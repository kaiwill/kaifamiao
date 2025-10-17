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
package com.kaifamiao.extra02;

import com.kaifamiao.extra02.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
@Slf4j
public class RagServiceTest {
    @Test
    public void testRetrieve(@Autowired RagService ragService) {
        Flux<ChatResponse> responseFlux = ragService.retrieve("你好,请为我介绍百炼 X1?");
        StringBuilder      sb           = new StringBuilder();
        responseFlux.toIterable().forEach(response -> {
            String text = response.getResult().getOutput().getText();
            sb.append(text);
            log.info("response: {}", text);
        });
        log.info("response: {}", sb);
    }

}