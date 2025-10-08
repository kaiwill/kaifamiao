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
package com.kaifamiao.chapter01.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WatcherToolService {
    @Tool(description = "根据城市名称查询当前天气")
    public String getWeather(@ToolParam(description = "城市名称，如 北京、上海") String city) {
        log.info("根据城市名称查询当前天气: {}", city);
        // 模拟调用高德/和风天气 API
        if ("西安".equals(city)) {
            return "西安，晴，气温 25°C，空气质量优";
        } else if ("上海".equals(city)) {
            return "上海，多云，气温 28°C，东南风 3 级";
        } else if ("北京".equals(city)) {
            return "北京，多云，气温 24°C，西南风 1 级";
        }
        return "未找到 " + city + " 的天气信息";
    }
}