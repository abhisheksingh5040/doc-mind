package com.zuci.doc_mind.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("DocMind — AI Document Intelligence & RAG backend")
                                .description("REST API for DocMind: Multi-format document ingestion, vector embeddings with PostgreSQL pgvector, and hybrid conversational Q&A with OpenAI.")
                                .version("1.0.0")
                                .contact(new Contact()
                                        .name("abhishek singh")
                                        .email("abhishekmech95@gmail.com")
                                        .url("https://abhishek.com")
                                )

                );
    }
}
