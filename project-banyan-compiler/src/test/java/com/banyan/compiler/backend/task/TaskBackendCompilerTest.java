package com.banyan.compiler.backend.task;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.ruleset.RuleSetBackendCompiler;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.RuleSetSemanticValidator;
import com.banyan.compiler.semantics.TaskSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskBackendCompilerTest {


    private static final TaskSemanticValidator validator = new TaskSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "task/semantic/valid";

    @Test
    public void compile_validtask_shouldProduceCompiledArtifact() throws JsonProcessingException {
        List<String> jsons =
                TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
        CompilationContext ctx = new CompilationContext();
        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);

                TaskBackendCompiler compiler = new TaskBackendCompiler();

                CompiledTaskArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
                ctx.register(compileEvidence);
                assertEquals(ArtifactType.Task, compileEvidence.type());

        }
        System.out.println(ctx.resolve(ArtifactType.Task,"task_with_actions",1).payload().toString());
        assertEquals("task_with_actions",ctx.resolve(ArtifactType.Task,"task_with_actions",1).id());

    }
}
