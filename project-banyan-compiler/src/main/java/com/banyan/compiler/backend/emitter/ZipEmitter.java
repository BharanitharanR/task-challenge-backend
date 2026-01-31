package com.banyan.compiler.backend.emitter;

import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.backend.outcome.CompilationRoot;
import com.banyan.compiler.enums.ArtifactType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.banyan.compiler.backend.emitter.ManifestHeader;

public class ZipEmitter implements ArtifactEmitter{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public boolean supports(CompilationRoot root) {
        return false;
    }

    @Override
    public void emit(CompilationOutcome outcome) {
        if (!outcome.isSuccess()) return;

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // 1. Create a clean workspace
            Path tempDir = Files.createTempDirectory("banyan_compilation_");
            ManifestHeader mfHeader = null;
            // Manifests.json
            List<String> files = new ArrayList<>();
            for (CompiledArtifact<?> artifact : outcome.getReachableArtifacts()) {
                // Get relative path: e.g., "CHALLENGE/1/my_artifact"
                String relativePathStr = getFileName(artifact) + ".json";
                if(artifact.type().equals(ArtifactType.Challenge)) {
                    mfHeader  = new ManifestHeader(artifact.id(), String.valueOf(artifact.version()), String.valueOf(artifact.metadata().getCompiledAtEpochMillis()),
                            artifact.metadata().getCompilerVersion());
                }
                // Resolve against temp directory: e.g., "/tmp/banyan_.../CHALLENGE/1/my_artifact.json"
                Path fullPath = tempDir.resolve(relativePathStr);

                // Create parent directories (CHALLENGE/1/)
                Files.createDirectories(fullPath.getParent());
                ;
                files.add(relativePathStr);
                // Serialize the file
                objectMapper.writeValue(fullPath.toFile(), artifact);
            }
           // Manifests json
           ManifestsJson mfJson = new ManifestsJson(mfHeader,files);
           objectMapper.writeValue(tempDir.resolve("manifests.json").toFile(), mfJson);
            // 2. Package everything into a ZIP in /target
            Path targetDir = Paths.get("target");
            if (Files.notExists(targetDir)) Files.createDirectories(targetDir);

            // data accepted by rule
            Path zipFile = targetDir.resolve("compilation_package.dar");
            createZipFromDirectory(tempDir, zipFile);

            // 3. Housekeeping
             cleanupTempDir(tempDir);

        } catch (IOException e) {
            throw new RuntimeException("Architectural Failure: Could not package artifacts", e);
        }
    }
    public String getFileName(CompiledArtifact<?> compiledArtifact) {
        // Removed the leading "/" for better relative pathing
        return String.join(File.separator,
                compiledArtifact.type().name(),
                String.valueOf(compiledArtifact.version()),
                compiledArtifact.id()
        );
    }


    private void createZipFromDirectory(Path sourceDir, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDir)
                    .filter(path -> {
                                return(!Files.isDirectory(path));}
                    )
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            // VITAL: This line actually moves the data into the ZIP
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Error adding file to zip", e);
                        }
                    });
        }
    }

    private void cleanupTempDir(Path tempDir) throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
