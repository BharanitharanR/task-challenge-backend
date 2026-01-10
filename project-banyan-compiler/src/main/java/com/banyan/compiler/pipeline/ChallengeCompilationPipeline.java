package com.banyan.compiler.pipeline;

import com.banyan.compiler.core.CompilationPipeline;
import com.banyan.compiler.lint.Linter;
import com.banyan.compiler.schema.ChallengeSchemaValidator;
import com.banyan.compiler.schema.SchemaValidator;
import com.banyan.compiler.semantics.SemanticValidator;

public class ChallengeCompilationPipeline implements CompilationPipeline {
    public SchemaValidator validator = new ChallengeSchemaValidator();
    @Override
    public SchemaValidator schemaValidator() {
        return this.validator;
    }

    @Override
    public SemanticValidator semanticValidator() {
        return null;
    }

    @Override
    public Linter lint() {
        return null;
    }
}
