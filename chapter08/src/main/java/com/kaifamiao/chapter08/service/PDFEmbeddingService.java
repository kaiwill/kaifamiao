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

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PDFEmbeddingService {
    private final VectorStore vectorStore;

    public PDFEmbeddingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processPDFAndStoreToMilvus(Resource pdfResource) {
        try {
            // 1. 读取PDF文档
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(ExtractedTextFormatter.builder().withNumberOfTopTextLinesToDelete(0)
                            .build())
                    .withPagesPerDocument(1)//如果设置为0，则表示所有页都变成一个文档
                    .build();

            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                    pdfResource, config);

            List<Document> documents = pdfReader.get();
            // 处理文档内容中的非UTF-8字符
            List<Document> cleanedDocuments = documents.stream()
                    .map(this::cleanNonUTF8Characters)
                    .collect(Collectors.toList());

            // 2. 文本分割
            TextSplitter   textSplitter   = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(cleanedDocuments);
//            for(Document doc:splitDocuments){
//                log.info("media:{}, metadata:{}  content:{}",doc.getMedia(),doc.getMetadata().keySet(),doc.getText());
//            }
            // 分批处理，每批不超过10个文档，避免超出嵌入模型的批量大小限制
            int batchSize = 10;
            for (int i = 0; i < splitDocuments.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, splitDocuments.size());
                List<Document> batch = splitDocuments.subList(i, endIndex);
                // 3. 存储到Milvus向量数据库（分批处理）
                vectorStore.add(batch);
                log.info("Processed batch {} to {}", i / batchSize + 1, (endIndex - 1) / batchSize + 1);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process PDF and store to Milvus", e);
        }
    }

    // 修改清理非UTF-8字符的方法以适应新的API
    private Document cleanNonUTF8Characters(Document document) {
        String text = document.getText();
        // 清理非UTF-8字符
        String cleanedText = text.replaceAll("[^\\p{Print}\\s]", "?");
        // 创建新的Document对象
        return new Document(cleanedText, document.getMetadata());
    }

}