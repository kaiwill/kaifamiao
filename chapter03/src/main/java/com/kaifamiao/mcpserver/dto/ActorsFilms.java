package com.kaifamiao.mcpserver.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

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
// @JsonPropertyOrder 用来指定 JSON 里的属性顺序（可选）
@JsonPropertyOrder({"actor", "movies"})
public record ActorsFilms(String actor,    // 演员名
                          List<String> movies  // 电影列表
) {
}
