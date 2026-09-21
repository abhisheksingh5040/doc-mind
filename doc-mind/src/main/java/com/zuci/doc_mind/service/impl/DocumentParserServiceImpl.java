package com.zuci.doc_mind.service.impl;

import com.zuci.doc_mind.exception.DocumentProcessingException;
import com.zuci.doc_mind.service.DocumentParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentParserServiceImpl implements DocumentParserService {

    @Override
    public List<Document> parse(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        log.info("Parsing document : {}, size: {} bytes, with content type: {}", fileName, file.getSize(), contentType);

        try {
            Resource resource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            if (fileName.toLowerCase().endsWith(".pdf") || contentType.toLowerCase().contains("pdf")) {
                return parsePdf(resource);
            } else {
                return parseGenericFile(resource);
            }

        } catch (IOException e) {
            log.error("Error while reading file : {}", fileName, e);
            throw new DocumentProcessingException("Could not read upload file : " + fileName, e);
        } catch (Exception e) {
            log.error("Error during document parsing : {}", fileName, e);
            throw new DocumentProcessingException("Failed to parse document content : " + fileName, e);
        }
    }

    private List<Document> parseGenericFile(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageBottomMargin(0)
                .build();
        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(resource, config);
        return documentReader.read();
    }

    private List<Document> parsePdf(Resource resource) {
        TikaDocumentReader documentReader = new TikaDocumentReader(resource);
        return documentReader.read();
    }
}
