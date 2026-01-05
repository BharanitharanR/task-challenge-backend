package com.banyan.compiler.registry;

import com.banyan.compiler.core.CompilationPipeline;
import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

public class CompilationPipelineRegistry {
    private final Map<String, CompilationPipeline> pipelines = new HashMap<>();

    public void register(@Nonnull String kind,@Nonnull CompilationPipeline pipeline) {
        this.pipelines.put(kind,pipeline);
    }
    public CompilationPipeline get(String kind) {
        return this.pipelines.get(kind);
    }


}
