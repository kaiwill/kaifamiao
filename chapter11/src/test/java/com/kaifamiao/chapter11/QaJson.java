package com.kaifamiao.chapter11;

import java.util.List;

//保存json文件中的数据
public record QaJson(String question, List<String> docs, String answer) {
}
