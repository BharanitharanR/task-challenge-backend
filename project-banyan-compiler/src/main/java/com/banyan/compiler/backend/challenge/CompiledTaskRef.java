package com.banyan.compiler.backend.challenge;

public record CompiledTaskRef(
        String taskId,
        int taskVersion
) {}
