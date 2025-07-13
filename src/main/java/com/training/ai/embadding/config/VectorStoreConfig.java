package com.training.ai.embadding.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {
    private final PgvectorConfig pgvectorConfig;

    @Bean
    public JdbcTemplate pgvectorJdbcTemplate(
            @Qualifier("pgvectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public VectorStore vectorStore(@Qualifier("pgvectorJdbcTemplate") JdbcTemplate jdbcTemplate,
                                   EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(pgvectorConfig.getDimensions())                    // Optional: defaults to model dimensions or 1536
                .distanceType(PgVectorStore.PgDistanceType.valueOf(pgvectorConfig.getDistanceType()))       // Optional: defaults to COSINE_DISTANCE
                .indexType(PgVectorStore.PgIndexType.valueOf(pgvectorConfig.getIndexType())/*HNSW*/)                     // Optional: defaults to HNSW
                .initializeSchema(pgvectorConfig.getInitializeSchema())              // Optional: defaults to false
                .schemaName(pgvectorConfig.getSchemaName())                // Optional: defaults to "public"
                .vectorTableName(pgvectorConfig.getTableName())     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(pgvectorConfig.getMaxDocumentBatchSize())         // Optional: defaults to 10000
                .build();
    }

/*    @Bean
    public JdbcTemplate pgmlJdbcTemplate(
            @Qualifier("pgmlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }*/

/*    @Bean
    public EmbeddingModel embeddingModel(@Qualifier("pgmlJdbcTemplate") JdbcTemplate jdbcTemplate) {

        return new PostgresMlEmbeddingModel(jdbcTemplate,
                PostgresMlEmbeddingOptions.builder()
                        .transformer("distilbert-base-uncased") // huggingface transformer model name.
                        .vectorType(PostgresMlEmbeddingModel.VectorType.PG_VECTOR) //vector type in PostgreSQL.
                        .kwargs(Map.of("device", "cpu")) // optional arguments.
                        .metadataMode(MetadataMode.EMBED) // Document metadata mode.
                        .build());
    }*/

}
