package com.banyan.compiler.core;

import com.banyan.compiler.lint.LintFinding;
import com.banyan.compiler.lint.Linter;
import com.banyan.compiler.lint.RuleLinter;
import com.banyan.compiler.schema.RuleSchemaValidator;
import com.banyan.compiler.schema.SchemaValidator;
import com.banyan.compiler.semantics.RuleSemanticValidator;
import com.banyan.compiler.semantics.SemanticValidator;

import java.util.List;

public interface CompilationPipeline {

    SchemaValidator schemaValidator();

    SemanticValidator semanticValidator();

    Linter lint() ;

    default CompilationResult compile(String dslJson)
    {
        List<String> schemaError = schemaValidator().validate(dslJson);
        if(!schemaError.isEmpty()) {
            return CompilationResult.failure(schemaError);
        }
        List<String> semanticError = semanticValidator().validate(dslJson);
        if(!semanticError.isEmpty()) {
            return CompilationResult.failure(semanticError);
        }

        List<LintFinding> lintFinding =  lint().lint(dslJson);

        List<String> warnings = lintFinding.stream().map(LintFinding::getMessage).toList();
        return CompilationResult.success(null,warnings);
    }
}
