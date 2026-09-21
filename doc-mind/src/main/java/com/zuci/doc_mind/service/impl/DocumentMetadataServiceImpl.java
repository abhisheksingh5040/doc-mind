package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.dto.DocumentResponseDto;
import com.zuci.doc_mind.repository.DocumentMetadataRepository;
import com.zuci.doc_mind.service.DocumentMetadatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentMetadataServiceImpl implements DocumentMetadatService {

    private final DocumentMetadataRepository documentMetadataRepository;

    @Override
    public DocumentResponseDto uploadAndProcess(MultipartFile file) {
        return null;
    }
}
