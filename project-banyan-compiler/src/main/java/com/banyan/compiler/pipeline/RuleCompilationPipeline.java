package com.banyan.compiler.pipeline;

import com.banyan.compiler.core.CompilationPipeline;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.lint.EvidenceTypeLinter;
import com.banyan.compiler.lint.LintFinding;
import com.banyan.compiler.lint.Linter;
import com.banyan.compiler.lint.RuleLinter;
import com.banyan.compiler.schema.EvidenceTypeSchemaValidator;
import com.banyan.compiler.schema.RuleSchemaValidator;
import com.banyan.compiler.schema.SchemaValidator;
import com.banyan.compiler.semantics.EvidenceTypeSemanticValidator;
import com.banyan.compiler.semantics.RuleSemanticValidator;
import com.banyan.compiler.semantics.SemanticValidator;

import java.util.List;

public class RuleCompilationPipeline implements CompilationPipeline {
    private final SchemaValidator schemaValidator =
            new RuleSchemaValidator();

    private final SemanticValidator semanticValidator = new RuleSemanticValidator();

    private final Linter linter = new RuleLinter();



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
