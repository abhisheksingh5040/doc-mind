package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.service.DocumentParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentParserServiceImpl implements DocumentParserService {

    @Override
    public List<Document> parse(MultipartFile file) {
        return List.of();
    }
}
