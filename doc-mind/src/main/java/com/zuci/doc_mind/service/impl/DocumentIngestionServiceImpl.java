package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.config.AppProperties;
import com.zuci.doc_mind.entity.DocumentMetadata;
import com.zuci.doc_mind.enums.DocumentStatus;
import com.zuci.doc_mind.exception.DocumentProcessingException;
import com.zuci.doc_mind.repository.DocumentMetadataRepository;
import com.zuci.doc_mind.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionServiceImpl implements DocumentIngestionService {

    private final VectorStore vectorStore;
    private final DocumentMetadataRepository documentMetadataRepository;
    private final AppProperties appProperties;

    @Override
    public int ingest(DocumentMetadata documentMetadata, List<Document> parsedDocs) {
        log.info("Ingesting documents [] id={}, name={}, pages={}]", documentMetadata.getId(), documentMetadata.getFileName(), parsedDocs.size());

        try {
            documentMetadata.setStatus(DocumentStatus.PROCESSING);
            documentMetadata.setTotalPages(parsedDocs.size());
//            1. Text chunking using TokenTextSplitter
            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                    .withChunkSize(appProperties.getRag().getChunkSize())
                    .withMinChunkSizeChars(appProperties.getRag().getMinChunkSizeChars())
                    .withMinChunkLengthToEmbed(appProperties.getRag().getMinChunkLengthToEmbed())
                    .withMaxNumChunks(appProperties.getRag().getMaxIntChunks())
                    .withKeepSeparator(true)
                    .build();

            List<Document> chunks = tokenTextSplitter.apply(parsedDocs);
            if (chunks.isEmpty()) {
                documentMetadata.setStatus(DocumentStatus.FAILED);
                documentMetadata.setErrorMessage("Document appears to be empty or unscannable");
                documentMetadataRepository.save(documentMetadata);
                return 0;
            }
//            2. Meta data enrichment on each chunk
            List<Document> enrichedChunks = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++){
                Document chunk = chunks.get(i);
                Map<String, Object> enrichedMetadata = new HashMap<>(chunk.getMetadata());
                enrichedMetadata.put("documentId", documentMetadata.getId().toString());
                enrichedMetadata.put("fileName", documentMetadata.getFileName());
                enrichedMetadata.put("contentType", documentMetadata.getContentType());
                enrichedMetadata.put("chunkIndex", i);
                Object pageNumber = chunk.getMetadata().get("page_number");
                if (pageNumber == null) {
                    pageNumber = chunk.getMetadata().get("pageNumber");

                }

                if (pageNumber != null) {
                    enrichedMetadata.put("pageNumber", pageNumber);
                }
                Document enrichedDoc = new Document(chunk.getText(), enrichedMetadata);
                enrichedChunks.add(enrichedDoc);
            }


//            3. write chunks and embedding to pg vector
            log.info("Writing {} vector chunks to PgVectorStore for document: {}", enrichedChunks.size(), documentMetadata.getFileName());
            vectorStore.add(enrichedChunks);

//            4. Update document status to index
            documentMetadata.setStatus(DocumentStatus.INDEXED);
            documentMetadata.setTotalChunks(enrichedChunks.size());
            documentMetadata.setErrorMessage(null);
            documentMetadataRepository.save(documentMetadata);
            log.info("Successfully indexed document [id={}, name={}, chunks={}]", documentMetadata.getId(), documentMetadata.getFileName(), enrichedChunks.size());

            return enrichedChunks.size();
        }catch (Exception ex){
            log.error("Failed to ingest document into vector store: {}", documentMetadata.getFileName(), ex);
            documentMetadata.setStatus(DocumentStatus.FAILED);
            documentMetadata.setErrorMessage(ex.getMessage());
            documentMetadataRepository.save(documentMetadata);
            throw new DocumentProcessingException("Failed to index document: " + ex.getMessage(), ex);
        }
    }
}
