package com.training.ai.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {
    private String model;
    private List<String> texts;
    @JsonProperty("input_type")
    private String inputType;
    @JsonProperty("embedding_types")
    private String[] embeddingTypes;
}
