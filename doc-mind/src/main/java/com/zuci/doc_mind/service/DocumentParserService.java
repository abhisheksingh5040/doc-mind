package com.zuci.doc_mind.service;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentParserService {
    List<Document> parse(MultipartFile file);
}
