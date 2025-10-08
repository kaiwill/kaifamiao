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
package com.kaifamiao.chapter09;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAudioSpeechApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SpringBootTest
@Slf4j
public class DashScopeAudioSpeechModelTest {
    @Test
    public void testAudioSpeechModel(@Autowired DashScopeAudioSpeechModel dashScopeAudioSpeechModel) throws IOException {
        // 创建一个 DashScopeAudioSpeechOptions 对象，并设置参数
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(DashScopeAudioSpeechApi.AudioSpeechModel.SAM_BERT_ZHICHU_V1.value) // 指定模型
                .requestText(DashScopeAudioSpeechApi.RequestTextType.PLAIN_TEXT)
                .voice("Cherry") // 音色
                .build();
        String text = "尊贵的VIP客户，本次为您节省15.52元";

        SpeechSynthesisPrompt prompt = new SpeechSynthesisPrompt(text, options);
        // 调用模型的方法，将文本转换为语音
        SpeechSynthesisResponse response = dashScopeAudioSpeechModel.call(prompt);

        // 将生成的语音保存到文件
        File file = new File(System.getProperty("user.dir") + "/output.mp3");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer byteBuffer = response.getResult().getOutput().getAudio();
            fos.write(byteBuffer.array());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}