package com.banyan.compiler.backend.rule;
import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceField;
import com.banyan.compiler.backend.spi.AbstractBackendCompiler;
import com.banyan.compiler.enums.EvidenceValueType;
import com.banyan.compiler.enums.RuleType;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class RuleBackendCompiler extends AbstractBackendCompiler<CompiledRuleArtifact> {
    @Override
    public CompiledRuleArtifact compile(JsonNode validatedDsl, CompilationContext context) {
        String id = readId(validatedDsl);
        int version = readVersion(validatedDsl);

        CompilationMetadata metadata = metadata(validatedDsl);

        JsonNode rule = validatedDsl.at("/spec");

        CompiledRule field =
                new CompiledRule(
                rule.get("input").asText(),
                rule.get("operator").asText(),
                extractValue(rule.get("value")),
                rule.get("type").asText()
        );
        return new CompiledRuleArtifact(id,version,field,metadata);
    }

    private Object extractValue(JsonNode node) {
        if (node.isInt()) return node.intValue();
        if (node.isLong()) return node.longValue();
        if (node.isDouble()) return node.doubleValue();
        if (node.isBoolean()) return node.booleanValue();
        if (node.isTextual()) return node.textValue();
        throw new IllegalStateException("UNSUPPORTED_RULE_VALUE");
    }

}
