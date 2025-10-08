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

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@SpringBootTest
@Slf4j
public class DashScopeImageModelTest {
    @Test
    public void testImageModel(@Autowired DashScopeImageModel dashScopeImageModel) {
        // 自己指定一个模型
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .withModel("wan2.5-t2i-preview") // withXXX 可以配置其它选项参数，比如宽度，高度，水印等
                // 水印
                .withWatermark(true)
                .build();
        String instructions = "一副典雅庄重的对联悬挂于厅堂之中，房间是个安静古典的中式布置，桌子上放着一些青花瓷，" +
                "对联上左书“义本生知人机同道善思新”，右书“通云赋智乾坤启数高志远”，" +
                " 横批“智启通义”，字体飘逸，" +
                "中间挂在一着一副中国风的画作，内容是岳阳楼。";
        ImagePrompt prompt = new ImagePrompt(instructions, options);

        ImageResponse response = dashScopeImageModel.call(prompt);
        log.info("response: {}", response);
        String url    = response.getResult().getOutput().getUrl();
        String base64 = response.getResult().getOutput().getB64Json();
        log.info("url: {}", url);
        File file = new File(System.getProperty("user.dir") + "/image.png");
        // 下载url 指定的图片，保存到file中
        try {
            FileUtils.copyURLToFile(new URL(url), file);
        } catch (IOException e) {
            log.error("下载图片失败", e);
        }
    }
}