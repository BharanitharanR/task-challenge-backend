package com.banyan.compiler.backend.context;

import com.banyan.compiler.backend.api.CompilationErrorCode;
import com.banyan.compiler.backend.api.CompilationException;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.compatibility.bootstrap.CompatibilityResolver;
import com.banyan.compiler.compatibility.bootstrap.CompilerBootstrapContext;
import com.banyan.compiler.enums.ArtifactType;
import com.banyan.compiler.enums.EvidenceValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.constraint.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class CompilationContext {
    @NotNull
    private final Map<String, CompiledArtifact> symbolTable= new HashMap<>();
    private final CompilerBootstrapContext compatibility;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path ARTIFACTS_DIR = Paths.get("/Users/bharani/Documents/task-challenge-backend/project-banyan-compiler"); // Adjust path
    private boolean eligibleForCompiledArtifact = false;
    public CompilationContext(CompilerBootstrapContext compatibility) {
        this.compatibility = compatibility;
    }
    public void eligibleForCompiledArtifact() {
        this.eligibleForCompiledArtifact = true;
    }
    public <A, B> CompatibilityResolver<A, B> compatibility(
            Class<A> left,
            Class<B> right
    ) {
        return compatibility.compatibility(left, right);
    }

    public void register(CompiledArtifact artifact){
        if(artifact.type().equals(ArtifactType.Challenge))
        {
            eligibleForCompiledArtifact();
        }
        symbolTable.put(key(artifact.type().toString(),artifact.id(),artifact.version()),artifact);
    }

    public CompiledArtifact resolve(ArtifactType type, String id, int version) throws CompilationException
    {
        if(!symbolTable.containsKey(key(type.toString(), id, version)))
        {
            throw new CompilationException(CompilationErrorCode.MISSING_DEPENDENCY,"KEY NOT FOUND");
        }
        return symbolTable.get(key(type.toString(), id, version)) ;
    }


    public void zipContext()
    {
        if(!eligibleForCompiledArtifact)
        {
            System.out.println("NOT_ELIGIBLE_FOR_COMPILED_ARTIFACT_CREATION");
        }
        else {
            try {

                for (String key : this.symbolTable.keySet()) {
                    CompiledArtifact artifact = this.symbolTable.get(key);
                    switch (artifact.type()) {
                        case ArtifactType.Task:
                        case ArtifactType.EvidenceType:
                        case ArtifactType.Rule:
                        case ArtifactType.Ruleset:
                        case ArtifactType.Challenge:
                        default:
                            writeToFile(artifact);

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void writeToFile(CompiledArtifact artifact){
        try {
            String jsonPayload = MAPPER.writeValueAsString(artifact.payload());
            Path filePath = ARTIFACTS_DIR.resolve("TMP_ARTIFACT/"+artifact.type().name() + "/" + artifact.id() + "_" + artifact.version() + ".json");
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, jsonPayload, StandardCharsets.UTF_8, CREATE_NEW);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
    public String key(String kind,String id,int version)
    {
        return kind+":"+id+":"+version;
    }
}
