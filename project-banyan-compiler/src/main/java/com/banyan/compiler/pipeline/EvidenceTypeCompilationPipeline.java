package com.banyan.compiler.pipeline;

import com.banyan.compiler.core.CompilationPipeline;
import com.banyan.compiler.lint.EvidenceTypeLinter;
import com.banyan.compiler.lint.Linter;
import com.banyan.compiler.schema.EvidenceTypeSchemaValidator;
import com.banyan.compiler.schema.SchemaValidator;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.SemanticValidator;

public class EvidenceTypeCompilationPipeline implements CompilationPipeline {
    private final SchemaValidator schemaValidator =
            new EvidenceTypeSchemaValidator();

    private final SemanticValidator semanticValidator = new EvidenceTypeSemanticValidator();

    private final Linter linter = new EvidenceTypeLinter();


    @Override
    public SchemaValidator schemaValidator() {
        return this.schemaValidator;
    }

    @Override
    public SemanticValidator semanticValidator() {
        return this.semanticValidator;
    }

    @Override
    public Linter lint() {
        return this.linter;
    }
}
