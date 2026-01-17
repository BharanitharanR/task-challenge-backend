package com.banyan.compiler.semantics;

import com.banyan.compiler.enums.TaskActionEnum;
import com.banyan.compiler.enums.TaskActionOn;
import com.banyan.compiler.enums.TaskActionsOnly;
import com.banyan.compiler.enums.TaskResulTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class TaskSemanticValidator implements SemanticValidator
{
    /* Task Semantics Bible
                TS40001 — rulesetRef must be non-empty
                TS40002 — resultType must be valid
                TS40003 — actions must be declarative (no params, no logic)
                TS40004 — action triggers must be valid (SUCCESS | FAILURE | ALWAYS)
                TS40005 — task must remain passive (no logic fields allowed)
     */
    private static final  ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> validate(String dslJson) {
        try {
            List<String> violations = new ArrayList<>();
            JsonNode root = mapper.readTree(dslJson);
            JsonNode spec = root.at("/spec");

            // TS20001: rulesetRef must be non-empty
            JsonNode rulesetRef = spec.get("rulesetRef");
            if (rulesetRef == null || !rulesetRef.isObject()) {
                violations.add("TASK_ERR 40001: rulesetRef must be non-empty");
            }
            if(rulesetRef.get("id") == null || (rulesetRef.get("id").isObject() || rulesetRef.get("id").isMissingNode() || rulesetRef.get("id").isArray() ))
                violations.add("TASK_ERR 40009: rulesetRef must have a valid ID");

            // TS20002: resultType must be valid
            JsonNode resultType = spec.get("resultType");
            if (resultType == null) {
                violations.add("TASK_ERR 40002: resultType must be non-empty");
            } else if (!TaskResulTypeEnum.contains(resultType.asText().trim())) {
                violations.add("TASK_ERR 40003: resultType must be valid");
            }

            // TS20003 / TS20004: actions (optional)
            JsonNode actions = spec.get("actions");
            if (actions != null) {
                if (!actions.isArray()) {
                    violations.add("TASK_ERR 40004: actions must be an array");
                } else {
                    for (JsonNode action : actions) {

                        if (!TaskActionOn.contains(action.get("on").asText())) {
                            violations.add("TASK_ERR 40005: action 'on' must be valid");
                        }

                        if (!action.get("emit").isTextual()) {
                            violations.add("TASK_ERR 40006: emit must be a string");
                        }

                        action.fieldNames().forEachRemaining(field -> {
                            if (!TaskActionsOnly.contains(field)) {
                                violations.add(
                                        "TASK_ERR 40007: invalid action field '" + field + "'"
                                );
                            }
                        });
                    }
                }
            }

            return violations;

        } catch (Exception e) {
            return List.of(
                    "TASK_SEMANTIC_PARSER_ERROR: " +
                            (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
            );
        }
    }
}

