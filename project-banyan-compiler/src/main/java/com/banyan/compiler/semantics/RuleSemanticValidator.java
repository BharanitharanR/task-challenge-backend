package com.banyan.compiler.semantics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RuleSemanticValidator implements SemanticValidator{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Set<String>  ALLOWED_RULE_TYPE = Set.of(
            "THRESHOLD",
            "EQUALITY",
            "RANGE"
    );

    private static final Set<String> NUMERIC_OPERATORS = Set.of(
            "<", "<=", ">", ">=", "==", "!="
    );

    private static final Set<String> EQUALITY_OPERATORS = Set.of(
            "==", "!="
    );

    @Override
    public List<String> validate(String dslJson) {
        try {
            List<String> semanticViolations = new ArrayList<>();
            JsonNode ruleNode = mapper.readTree(dslJson);

            JsonNode spec = ruleNode.at("/spec");
            if (spec.isMissingNode() || !spec.isObject()) {
                semanticViolations.add("RULE_ERR 20000: spec object is missing or invalid");
                return semanticViolations;
            }

            String type = spec.at("/type").asText(null);
            String input = spec.at("/input").asText(null);
            String operator = spec.at("/operator").asText(null);
            JsonNode valueNode = spec.at("/value");

            if (type == null || !ALLOWED_RULE_TYPE.contains(type)) {
                semanticViolations.add(
                        "RULE_ERR 20001: Invalid rule type. Allowed values: " + ALLOWED_RULE_TYPE
                );
            }

            if (input == null || input.isBlank()) {
                semanticViolations.add(
                        "RULE_ERR 20002: Rule input must be a non-empty value"
                );
            }

            if (operator == null || operator.isBlank()) {
                semanticViolations.add(
                        "RULE_ERR 20003: Rule operator must be specified"
                );
            } else {
                if (valueNode.isNumber() && !NUMERIC_OPERATORS.contains(operator)) {
                    semanticViolations.add(
                            "RULE_ERR 20004: Numeric operators can be used only with numeric values"
                    );
                }
                if ((valueNode.isTextual() || valueNode.isBoolean())
                        && !EQUALITY_OPERATORS.contains(operator)) {
                    semanticViolations.add(
                            "RULE_ERR 20005: Only == or != allowed for boolean/string values"
                    );
                }
                if(! (valueNode.isNumber() || valueNode.isTextual() || valueNode.isBoolean()))
                    semanticViolations.add(
                            "RULE_ERR 20006:Unsupported type of value"
                    );
            }

            return semanticViolations;
        } catch (Exception e) {
            return List.of(
                    "RULE_SCHEMA_SEMANTIC_PARSER_ERROR: " +
                            (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
            );
        }
    }

}
