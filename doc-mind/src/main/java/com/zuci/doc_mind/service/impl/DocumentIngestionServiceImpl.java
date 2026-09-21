package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.entity.DocumentMetadata;
import com.zuci.doc_mind.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionServiceImpl implements DocumentIngestionService {

    @Override
    public int ingest(DocumentMetadata documentMetadata, List<Document> parsedDocs) {
        return 0;
    }
}
