package com.banyan.compiler.backend.task;

import com.banyan.compiler.enums.TaskActionEnum;

public record TaskActionRecord(
        TaskActionEnum taskActionOn,
        String action
) {
}
