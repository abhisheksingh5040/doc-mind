package com.zuci.doc_mind.service;

import com.zuci.doc_mind.dto.DocumentResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentMetadatService {
    public DocumentResponseDto uploadAndProcess(MultipartFile file);
}
