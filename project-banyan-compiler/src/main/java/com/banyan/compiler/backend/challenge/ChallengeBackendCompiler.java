package com.banyan.compiler.backend.challenge;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.spi.AbstractBackendCompiler;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.backend.task.TaskActionRecord;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.TaskActionEnum;
import com.banyan.compiler.enums.TaskResulTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChallengeBackendCompiler extends AbstractBackendCompiler<CompiledChallengeArtifact> {
    @Override
    public CompiledChallengeArtifact compile(JsonNode validatedDsl, CompilationContext context) {
        String id = readId(validatedDsl);
        int version = readVersion(validatedDsl);
        CompilationMetadata metadata = metadata(validatedDsl);
        JsonNode spec = validatedDsl.get("spec");
        if(spec.isMissingNode() || spec.isEmpty()) {
            return null; // Will figure out a better return
        }
        List<CompiledTaskRef> compiledTaskRefs = new ArrayList<>();
        JsonNode TaskRefs = spec.get("tasks");
        for(JsonNode TaskRef: TaskRefs)
        {
            String taskId = TaskRef.get("id").asText();
            int taskVersion = TaskRef.get("version").asInt();
            // HARD dependency check
            try {
                CompiledArtifact<CompiledTaskArtifact> taskRef = context.resolve(
                        ArtifactType.Task,
                        taskId,
                        taskVersion
                );
                if(Boolean.parseBoolean(taskRef.id()))
                {
                    System.out.println("Empty");
                }
            }
            catch (Exception e)
            {
                return null;
            }

            compiledTaskRefs.add(new CompiledTaskRef(taskId, taskVersion));
        }
        return new CompiledChallengeArtifact(
                id,version,new CompiledChallenge(compiledTaskRefs),metadata
        );
    }
}
