package com.zuci.doc_mind.service;

import com.zuci.doc_mind.entity.DocumentMetadata;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DocumentIngestionService {
    int ingest(DocumentMetadata documentMetadata, List<Document> parsedDocs);
}
