package com.banyan.compiler.backend.task;

import com.banyan.compiler.backend.ruleset.RuleRefNode;
import com.banyan.compiler.enums.TaskActionOn;
import com.banyan.compiler.enums.TaskResulTypeEnum;

import java.util.List;

/*
    "rulesetRef": "login_ruleset",
    "resultType": "BOOLEAN",
    "actions": [
      { "on": "FAILURE", "emit": "LOCK_ACCOUNT" },
      { "on": "ALWAYS", "emit": "AUDIT_EVENT" }
    ],
    "description": "Login security task"

 */
public record CompiledTask(
        String ruleSetId,
        String rulesetVersion,
        TaskResulTypeEnum taskType,
        List<TaskActionRecord> taskActions,
        String description
) {
}
