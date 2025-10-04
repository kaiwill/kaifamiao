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
package com.kaifamiao.chapter08.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyEmbeddingService {
    private final EmbeddingModel embeddingModel;

    public MyEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 批量计算文本的嵌入向量
     *
     * @param texts 文本
     * @return 嵌入向量
     */
    public List<float[]> embed(List<String> texts) {
        return embeddingModel.embed(texts);
    }

    public double cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("向量维度必须一致");
        }
        // 点积
        double dotProduct = 0.0;
        double norm1      = 0.0;
        double norm2      = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            //点积计算：通过循环累加每个维度上的乘积累积得到点积值。
            dotProduct += vec1[i] * vec2[i];
            //模长平方计算：分别对两个向量各维度进行平方并求和，得到了各自的模长平方。
            norm1 += Math.pow(vec1[i], 2);
            norm2 += Math.pow(vec2[i], 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0; // 避免除零
        }
        // 余弦相似度计算：将点积除以两个向量的模长的乘积，得到余弦相似度值。
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}