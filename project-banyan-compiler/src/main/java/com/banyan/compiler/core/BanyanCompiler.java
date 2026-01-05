package com.banyan.compiler.core;

import com.banyan.compiler.lint.EvidenceTypeLinter;
import com.banyan.compiler.lint.LintFinding;
import com.banyan.compiler.lint.Linter;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.banyan.compiler.schema.EvidenceTypeSchemaValidator;
import com.banyan.compiler.schema.SchemaValidator;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.SemanticValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public final class BanyanCompiler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final CompilationPipelineRegistry registry;

    public BanyanCompiler(CompilationPipelineRegistry registry) {
        this.registry = registry;
    }

    public CompilationResult compile(String dslJson)  {
        try
        {
            JsonNode dslJsonNode  = mapper.readTree(dslJson);
            String kind = dslJsonNode.at("/kind").asText(null);
            if (kind == null || kind.isBlank()) {
                return CompilationResult.failure(
                        List.of("BANYAN_COMPILER_ERROR 00001: Missing or empty 'kind' field")
                );
            }
            CompilationPipeline pipeline = this.registry.get(kind);
            if (pipeline == null) {
                return CompilationResult.failure(
                        List.of("BANYAN_COMPILER_ERROR 00002: No pipeline registered for kind: " + kind)
                );
            }
           return pipeline.compile(dslJson);
        }
        catch(Exception e)
        {
            return  CompilationResult.failure(List.of("BANYAN_COMPILER_ERROR 00000:"+ (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())));
        }

    }
}
