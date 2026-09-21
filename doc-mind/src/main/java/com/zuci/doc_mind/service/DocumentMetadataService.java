package com.zuci.doc_mind.service;

import com.zuci.doc_mind.dto.DocumentResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface DocumentMetadataService {
    public DocumentResponseDto uploadAndProcess(MultipartFile file);
}
