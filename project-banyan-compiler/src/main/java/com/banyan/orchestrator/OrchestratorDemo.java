package com.banyan.orchestrator;

import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.enums.ArtifactType;

/**
 * Demo class showing how to use the Orchestrator.
 * This demonstrates the complete compilation workflow.
 */
public class OrchestratorDemo {
    
    public static void main(String[] args) {
        // Create orchestrator instance
        Orchestrator orchestrator = new Orchestrator();
        
        // Example usage - compile a challenge from a ZIP file
        //String zipFilePath = "/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/sample_challenge_source_zip/challenge_source.zip";
        String zipFilePath = "/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/banyan-sources.zip";
        String outputDir = "/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/sample_challenge_source_zip/output";
        ArtifactType rootType = ArtifactType.Challenge;
        String rootId = "unique_task_challenge";
        int rootVersion = 1;
        
        try {
            // Execute orchestration
            CompilationResult result = orchestrator.orchestrate(
                zipFilePath, 
                outputDir, 
                rootType, 
                rootId, 
                rootVersion
            );
            
            if (result.isSuccess()) {
                System.out.println("Compilation successful!");
                
                // Extract outcome and report
                Object resultData = result.getArtifact();
                if (resultData instanceof java.util.Map) {
                    java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) resultData;
                    CompilationOutcome outcome = (CompilationOutcome) resultMap.get("outcome");
                    CompilationReport report = (CompilationReport) resultMap.get("report");
                    
                    System.out.println("Outcome: " + outcome);
                    System.out.println("Report: " + report);
                }
            } else {
                System.out.println("Compilation failed:");
                result.getErrors().forEach(System.out::println);
            }
            
        } catch (Exception e) {
            System.err.println("Orchestration error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
