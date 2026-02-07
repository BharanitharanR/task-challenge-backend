package com.banyan.orchestrator;

import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.CompilationState;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class OrchestratorDemoTest {

    private static final String SAMPLE_ZIP = "sample_challenge_source_zip/challenge_source.zip";
    private static final String BANYAN_SOURCES_ZIP = "banyan-sources.zip";

    @Test
    void orchestrate_sampleChallengeZip_success() throws Exception {
        Orchestrator orchestrator = new Orchestrator();
        String zipFilePath = resolveResourcePath(SAMPLE_ZIP);
        String outputDir = resolveResourcePath("sample_challenge_source_zip");

        Path darPath = Paths.get("target", "compilation_package.dar");
        Files.deleteIfExists(darPath);

        CompilationResult result = orchestrator.orchestrate(
                zipFilePath,
                outputDir,
                ArtifactType.Challenge,
                "unique_task_challenge",
                1
        );

        assertTrue(result.isSuccess(), () -> "Expected success but got errors: " + result.getErrors());
        assertNotNull(result.getArtifact());

        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result.getArtifact();
        CompilationOutcome outcome = (CompilationOutcome) resultMap.get("outcome");
        CompilationReport report = (CompilationReport) resultMap.get("report");

        assertNotNull(outcome);
        assertTrue(outcome.isSuccess());
        assertTrue(outcome.getReachableArtifacts().size() > 0);

        assertNotNull(report);
        assertEquals(CompilationState.COMPLETED, report.getState());
        assertNotNull(report.getTotalDuration());

        assertTrue(Files.exists(darPath), "Expected DAR file to be created.");
        assertTrue(Files.size(darPath) > 0, "DAR file should not be empty.");
    }

    @Test
    void orchestrate_banyanSourcesZip_failure() throws Exception {
        Orchestrator orchestrator = new Orchestrator();
        String zipFilePath = resolveResourcePath(BANYAN_SOURCES_ZIP);
        String outputDir = resolveResourcePath(".");

        CompilationResult result = orchestrator.orchestrate(
                zipFilePath,
                outputDir,
                ArtifactType.Challenge,
                "CHALLENGE",
                1
        );

        assertFalse(result.isSuccess());
        assertFalse(result.getErrors().isEmpty(), "Expected errors for invalid input sources.");
    }

    private static String resolveResourcePath(String resource) throws Exception {
        return Paths.get(
                Objects.requireNonNull(ClassLoader.getSystemResource(resource)).toURI()
        ).toString();
    }
}