package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.dto.DocumentResponseDto;
import com.zuci.doc_mind.entity.DocumentMetadata;
import com.zuci.doc_mind.enums.DocumentStatus;
import com.zuci.doc_mind.repository.DocumentMetadataRepository;
import com.zuci.doc_mind.service.DocumentMetadataService;
import com.zuci.doc_mind.service.DocumentIngestionService;
import com.zuci.doc_mind.service.DocumentParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentMetadataServiceImpl implements DocumentMetadataService {

    private final DocumentMetadataRepository documentMetadataRepository;
    private final DocumentParserService parserService;
    private final DocumentIngestionService ingestionService;

    @Override
    public DocumentResponseDto uploadAndProcess(MultipartFile file) {

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // document meta data create
        DocumentMetadata documentMetadata = DocumentMetadata.builder()
                .fileName(fileName)
                .contentType(contentType)
                .fileSize(file.getSize())
                .status(DocumentStatus.UPLOADING)
                .createdAt(LocalDateTime.now())
                .build();

        // Save the document metadata
        documentMetadata = documentMetadataRepository.save(documentMetadata);

        // parse the file
        List<Document> parsedDocs = parserService.parse(file);

        // ingest service
        int chunksCreated = ingestionService.ingest(documentMetadata, parsedDocs);
        return DocumentResponseDto.builder()
                .id(documentMetadata.getId())
                .fileName(documentMetadata.getFileName())
                .fileSize(documentMetadata.getFileSize())
                .chunksCreated(chunksCreated)
                .status(documentMetadata.getStatus())
                .message("Document successfully processed and uploaded")
                .build();
    }
}
