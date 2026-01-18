package com.banyan.compiler.backend.challenge;

import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.backend.task.TaskBackendCompiler;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.semantics.ChallengeSemanticValidator;
import com.banyan.compiler.semantics.TaskSemanticValidator;
import com.banyan.compiler.testutil.TestResourceLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeBackendCompilerTest {
    private static final ChallengeSemanticValidator validator = new ChallengeSemanticValidator();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VALID_RESOURCE = "challenge/semantic/valid";
    private static final String TASK_VALID_RESOURCE = "task/semantic/valid";
    static List<String> jsons = new ArrayList<>();
    static List<String> taskJsons = new ArrayList<>();
    static CompilationContext ctx = new CompilationContext();
@BeforeAll
static void register() throws JsonProcessingException {

     jsons= TestResourceLoader.loadJsonFiles(VALID_RESOURCE);
    taskJsons = TestResourceLoader.loadJsonFiles(TASK_VALID_RESOURCE);
    for (String json : taskJsons) {
        TaskBackendCompiler compiler = new TaskBackendCompiler();
        CompiledTaskArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
        ctx.register(compileEvidence);
    }

}
    @Test
    public void compile_validChallenge_shouldProduceCompiledArtifact() throws JsonProcessingException {

        for (String json : jsons) {
            List<String> errors = validator.validate(json);
            assertTrue(errors.isEmpty(), "Expected no errors but got: " + errors);

                ChallengeBackendCompiler compiler = new ChallengeBackendCompiler();

                CompiledChallengeArtifact compileEvidence = compiler.compile(mapper.readTree(json), ctx);
                ctx.register(compileEvidence);
                assertEquals(ArtifactType.Challenge, compileEvidence.type());

        }
        System.out.println(ctx.resolve(ArtifactType.Challenge,"unique_task_challenge",1).payload().toString());
        assertEquals("unique_task_challenge",ctx.resolve(ArtifactType.Challenge,"unique_task_challenge",1).id());

    }
}
