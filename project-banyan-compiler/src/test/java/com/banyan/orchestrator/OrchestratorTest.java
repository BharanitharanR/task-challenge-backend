package com.banyan.orchestrator;

import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.CompilationState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Orchestrator component.
 * Tests the complete compilation workflow and individual components.
 */
public class OrchestratorTest {

    @Test
    public void testSourceUnitCreation() throws Exception {
        // Test SourceUnit creation from valid JSON
        String validJson = """
                {
                  "kind": "Rule",
                  "id": "within_business_hours",
                  "version": 2,
                  "spec": {
                    "evidenceTypeRef": {
                      "id": "CONSOLIDATED_EVIDENCES",
                      "version": 1
                    },
                    "type": "EQUALITY",
                    "input": "businessHours",
                    "operator": "==",
                    "value": true
                  }
                }
            """;

        SourceUnit unit = new SourceUnit(new ObjectMapper().readTree(validJson));
        
        assertEquals(ArtifactType.Rule, unit.type());
        assertEquals("within_business_hours", unit.id());
        assertEquals(2, unit.version());
        assertNotNull(unit.content());
    }

    @Test
    public void testSourceUnitCreationWithMissingFields() {
        // Test SourceUnit creation with missing kind field
        String invalidJson = """
            {
                "id": "test_rule",
                "version": 1,
                "spec": {}
            }
            """;

        assertThrows(IllegalArgumentException.class, () -> {
            new SourceUnit(new ObjectMapper().readTree(invalidJson));
        });
    }

    @Test
    public void testSourceLibraryCreation() {
        // Test SourceLibrary creation from JSON sources
        String json1 = """
            {
                "kind": "Rule",
                "id": "rule1",
                "version": 1,
                "spec": {}
            }
            """;

        String json2 = """
            {
                "kind": "EvidenceType",
                "id": "evidence1",
                "version": 1,
                "spec": {}
            }
            """;

        List<String> jsonSources = List.of(json1, json2);
        SourceLibrary library = SourceLibrary.fromJsonSources(jsonSources);

        assertEquals(2, library.size());
        assertTrue(library.hasSources(ArtifactType.Rule));
        assertTrue(library.hasSources(ArtifactType.EvidenceType));
        assertFalse(library.hasSources(ArtifactType.Challenge));

        assertEquals(1, library.sources(ArtifactType.Rule).size());
        assertEquals(1, library.sources(ArtifactType.EvidenceType).size());

        assertTrue(library.source(ArtifactType.Rule, "rule1", 1).isPresent());
        assertTrue(library.source(ArtifactType.EvidenceType, "evidence1", 1).isPresent());
        assertFalse(library.source(ArtifactType.Rule, "nonexistent", 1).isPresent());
    }

    @Test
    public void testCompilationReportBuilder() {
        // Test CompilationReport builder
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(Duration.ofSeconds(10));

        CompilationReport report = CompilationReport.builder()
                .startTime(startTime)
                .endTime(endTime)
                .sourceParsingDuration(Duration.ofMillis(1000))
                .frontendCompilationDuration(Duration.ofMillis(3000))
                .backendCompilationDuration(Duration.ofMillis(4000))
                .outcomeBuildingDuration(Duration.ofMillis(1000))
                .emissionDuration(Duration.ofMillis(1000))
                .sourceCounts(Map.of(ArtifactType.Rule, 5, ArtifactType.EvidenceType, 3))
                .compiledArtifactCounts(Map.of(ArtifactType.Rule, 5, ArtifactType.EvidenceType, 3))
                .totalReachableArtifacts(8)
                .totalErrors(0)
                .totalWarnings(2)
                .state(CompilationState.COMPLETED)
                .build();

        assertEquals(startTime, report.getStartTime());
        assertEquals(endTime, report.getEndTime());
        assertEquals(Duration.ofSeconds(10), report.getTotalDuration());
        assertEquals(Duration.ofMillis(1000), report.getSourceParsingDuration());
        assertEquals(Duration.ofMillis(3000), report.getFrontendCompilationDuration());
        assertEquals(Duration.ofMillis(4000), report.getBackendCompilationDuration());
        assertEquals(Duration.ofMillis(1000), report.getOutcomeBuildingDuration());
        assertEquals(Duration.ofMillis(1000), report.getEmissionDuration());
        assertEquals(2, report.getSourceCounts().size());
        assertEquals(5, report.getSourceCounts().get(ArtifactType.Rule));
        assertEquals(3, report.getSourceCounts().get(ArtifactType.EvidenceType));
        assertEquals(2, report.getCompiledArtifactCounts().size());
        assertEquals(5, report.getCompiledArtifactCounts().get(ArtifactType.Rule));
        assertEquals(3, report.getCompiledArtifactCounts().get(ArtifactType.EvidenceType));
        assertEquals(8, report.getTotalReachableArtifacts());
        assertEquals(0, report.getTotalErrors());
        assertEquals(2, report.getTotalWarnings());
        assertEquals(CompilationState.COMPLETED, report.getState());
    }

    @Test
    public void testOrchestratorCreation() {
        // Test Orchestrator creation and registration
        Orchestrator orchestrator = new Orchestrator();

        // Verify orchestrator is created successfully
        assertNotNull(orchestrator);

        // Test OrchestratorContext methods (should throw UnsupportedOperationException)
        assertThrows(UnsupportedOperationException.class, () -> {
            orchestrator.sources(ArtifactType.Rule);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            orchestrator.source(ArtifactType.Rule, "test", 1);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            orchestrator.hasSources(ArtifactType.Rule);
        });
    }

    @Test
    public void testZipFileParserWithInvalidFile() {
        // Test ZipFileParser with non-existent file
        assertThrows(java.io.FileNotFoundException.class, () -> {
            ZipFileParser.parseZipFile("/nonexistent/file.zip");
        });
    }

    @Test
    public void testSourceLibraryWithInvalidJson() {
        // Test SourceLibrary with invalid JSON
        String invalidJson = "invalid json content";

        assertThrows(IllegalArgumentException.class, () -> {
            SourceLibrary.fromJsonSources(List.of(invalidJson));
        });
    }

    @Test
    public void testSourceLibraryWithDuplicateArtifacts() {
        // Test SourceLibrary with duplicate artifacts (same type, id, version)
        String json1 = """
            {
                "kind": "Rule",
                "id": "rule1",
                "version": 1,
                "spec": {}
            }
            """;

        String json2 = """
            {
                "kind": "Rule",
                "id": "rule1",
                "version": 1,
                "spec": {}
            }
            """;

        List<String> jsonSources = List.of(json1, json2);
        SourceLibrary library = SourceLibrary.fromJsonSources(jsonSources);

        // Should only contain one artifact (the second one overwrites the first)
        assertEquals(1, library.sources(ArtifactType.Rule).size());
    }

    @Test
    public void testSourceLibraryWithDifferentVersions() {
        // Test SourceLibrary with same artifact but different versions
        String json1 = """
            {
                "kind": "Rule",
                "id": "rule1",
                "version": 1,
                "spec": {}
            }
            """;

        String json2 = """
            {
                "kind": "Rule",
                "id": "rule1",
                "version": 2,
                "spec": {}
            }
            """;

        List<String> jsonSources = List.of(json1, json2);
        SourceLibrary library = SourceLibrary.fromJsonSources(jsonSources);

        assertEquals(2, library.sources(ArtifactType.Rule).size());
        assertTrue(library.source(ArtifactType.Rule, "rule1", 1).isPresent());
        assertTrue(library.source(ArtifactType.Rule, "rule1", 2).isPresent());
    }
}