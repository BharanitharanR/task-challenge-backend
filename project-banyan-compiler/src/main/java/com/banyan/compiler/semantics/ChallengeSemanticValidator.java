package com.banyan.compiler.semantics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ChallengeSemanticValidator implements SemanticValidator {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> validate(String dslJson) {
        List<String> violations = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(dslJson);

            JsonNode spec = root.at("/spec");
            if (spec.isMissingNode()) {
                violations.add("CH_ERR_50000: spec object is missing");
                // return violations;
            }

            JsonNode tasksNode = spec.at("/tasks");

            // CH-200: must contain at least one task
            if (!tasksNode.isArray() || tasksNode.size() == 0) {
                violations.add(
                        "CH_ERR_50001: Challenge must contain at least one task"
                );

            }

            Set<String> uniqueTaskIds = new HashSet<>();

            for (JsonNode taskRefNode : tasksNode) {

                // CH-201: task reference must be a non-empty string
                if (!taskRefNode.isObject()
                        || taskRefNode.isMissingNode()) {
                    violations.add(
                            "CH_ERR_50002: Task reference must be a non-empty string"
                    );
                    continue;
                }

                String taskId = taskRefNode.at("/id").asText();

                // CH-201: no duplicate tasks
                if (!uniqueTaskIds.add(taskId)) {
                    violations.add(
                            "CH_ERR_50003: Duplicate task reference not allowed: " + taskId
                    );
                }

                int version = taskRefNode.at("/version").asInt();
                // CH-201: no duplicate tasks
                if ( version <= 0 ) {
                    violations.add(
                            "CH_ERR_50006: Empty /INcorrect versions of Task not allowed " + taskId
                    );
                }
            }

            // CH-800: challenge must be passive (no executable fields)
            if (spec.has("actions")
                    || spec.has("operator")
                    || spec.has("expression")) {
                violations.add(
                        "CH_ERR_50004: Challenge must not contain logic or actions"
                );
            }



        } catch (Exception e) {
            violations.add(
                    "CH_SEMANTIC_PARSER_ERROR: " +
                            (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
            );
        }
        return violations;
    }
}
