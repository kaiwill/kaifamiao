package com.kaifamiao.chapter01.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

// @JsonPropertyOrder 用来指定 JSON 里的属性顺序（可选）
@JsonPropertyOrder({"actor", "movies"})
public record ActorsFilms(String actor,    // 演员名
                          List<String> movies  // 电影列表
) {
}
