package com.banyan.orchestrator;

import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.challenge.ChallengeBackendCompiler;
import com.banyan.compiler.backend.challenge.CompiledChallengeArtifact;
import com.banyan.compiler.backend.context.CompilationContext;
import com.banyan.compiler.backend.emitter.ArtifactEmitter;
import com.banyan.compiler.backend.emitter.ZipEmitter;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.evidence.EvidenceBackendCompiler;
import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.backend.outcome.CompilationOutcomeBuilder;
import com.banyan.compiler.backend.outcome.CompilationRoot;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.rule.RuleBackendCompiler;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.ruleset.RuleSetBackendCompiler;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.backend.task.TaskBackendCompiler;
import com.banyan.compiler.compatibility.bootstrap.CompilerCompatibilityBootstrap;
import com.banyan.compiler.core.BanyanCompiler;
import com.banyan.compiler.core.CompilationResult;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.pipeline.*;
import com.banyan.compiler.registry.CompilationPipelineRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;



public class InitiateCompilation {

    private static final ObjectMapper mapper = new ObjectMapper();
    public class UnzipExample {
        private static String readFile(Path path) {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

            public static List<String> loadJsonFiles(String resourcePath) {
                try {
                    Path  dir = Paths.get(resourcePath);

                    return Files.list(dir)
                            .filter(p -> p.toString().endsWith(".json"))
                            .map(UnzipExample::readFile)
                            .collect(Collectors.toList());

                } catch (IOException  e) {
                    throw new RuntimeException("Failed to load test resources", e);
                }
            }

        public static void unzip(String zipFilePath, String destDir) throws IOException {
            Path targetDir = Paths.get(destDir).toAbsolutePath().normalize();

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    Path newPath = targetDir.resolve(entry.getName()).normalize();

                    // Security check: Zip Slip protection
                    if (!newPath.startsWith(targetDir)) {
                        throw new IOException("Entry is outside of the target dir: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
            }
        }
    }
    /*
    public static void main(String args[]) {
         /*
            Accepts the input containing a zip file
         *

        try {
            UnzipExample.unzip("/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/sample_challenge_source_zip/challenge_source.zip", "/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/sample_challenge_source_zip/unzipped_file_sources");
            CompilationPipelineRegistry registry = new CompilationPipelineRegistry();
            BanyanCompiler compiler;
            ///  Register artifacts
            registry.register("Rule", new RuleCompilationPipeline());
            registry.register("Ruleset", new RuleSetCompilationPipeline());
            registry.register("EvidenceType", new EvidenceTypeCompilationPipeline());
            registry.register("Task", new TaskCompilationPipeline());
            registry.register("Challenge", new ChallengeCompilationPipeline());
            compiler = new BanyanCompiler(registry);
            List<String> dslFiles = UnzipExample.loadJsonFiles("/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler/src/test/resources/sample_challenge_source_zip/unzipped_file_sources");
            List<CompilationResult> resultAggregator = new ArrayList<>();
            for (String dsl : dslFiles) {
                resultAggregator.add(compiler.compile(dsl));
            }
            List<Object> artifacts =
                    resultAggregator.stream()
                            .filter(CompilationResult::isSuccess)
                            .map(CompilationResult::getArtifact)
                            .toList();
            List<String> dataStream = artifacts.stream().map(String::valueOf).toList();
            CompilationContext ctx = new CompilationContext(CompilerCompatibilityBootstrap.bootstrap());
            for (String data : dataStream) {
                try {

                    JsonNode dslJsonNode = mapper.readTree(data);
                    String kind = dslJsonNode.at("/kind").asText(null);


                    // Evidence-type Registration
                    switch (ArtifactType.valueOf(kind)) {

                        case ArtifactType.EvidenceType -> {
                            EvidenceBackendCompiler evTypeCompiler = new EvidenceBackendCompiler();
                            CompiledEvidenceTypeArtifact compileEvidence = evTypeCompiler.compile(dslJsonNode, ctx);
                            ctx.register(compileEvidence);
                        }

                        case ArtifactType.Task -> {
                            TaskBackendCompiler taskCompiler = new TaskBackendCompiler();
                            CompiledTaskArtifact compileEvidence = taskCompiler.compile(dslJsonNode, ctx);
                            ctx.register(compileEvidence);
                        }

                        case ArtifactType.Challenge -> {
                            ChallengeBackendCompiler challengeCompiler = new ChallengeBackendCompiler();
                            CompiledChallengeArtifact compileEvidence = challengeCompiler.compile(dslJsonNode, ctx);
                            ctx.register(compileEvidence);
                        }

                        case ArtifactType.Rule -> {
                            RuleBackendCompiler ruleBackendCompiler = new RuleBackendCompiler();
                            CompiledRuleArtifact compileEvidence = ruleBackendCompiler.compile(dslJsonNode, ctx);
                            ctx.register(compileEvidence);
                        }

                        case ArtifactType.Ruleset -> {
                            RuleSetBackendCompiler ruleSetBackendCompiler = new RuleSetBackendCompiler();
                            CompiledRulesetArtifact compileEvidence = ruleSetBackendCompiler.compile(dslJsonNode, ctx);
                            ctx.register(compileEvidence);
                        }



                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
            // freeze
            ctx.freeze();
            CompilationOutcomeBuilder builder  = new CompilationOutcomeBuilder(ctx,new CompilationRoot(
                    ArtifactType.Challenge,
                    "unique_task_challenge",
                    1
            ));
            CompilationOutcome outcome = builder.build();
            if(outcome.isSuccess())
            {
                for(CompiledArtifact<?> artifact: outcome.getReachableArtifacts())
                {
                    System.out.println(artifact.version()+":"+artifact.id()+":"+artifact.type().toString());
                }
            }

            ArtifactEmitter emitter = new ZipEmitter();
            emitter.emit(outcome);
        }
        catch(Exception e)
        {
            System.out.println("Errror"+e.getMessage());
        }
    }

     */
}
