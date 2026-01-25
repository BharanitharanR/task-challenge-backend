package com.banyan.compiler.backend.emitter;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.banyan.compiler.backend.challenge.CompiledChallenge;
import com.banyan.compiler.backend.challenge.CompiledChallengeArtifact;
import com.banyan.compiler.backend.evidence.CompiledEvidenceType;
import com.banyan.compiler.backend.outcome.CompilationOutcome;
import com.banyan.compiler.backend.outcome.CompilationRoot;
import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.ruleset.CompiledRuleset;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.task.CompiledTask;
import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.compiler.enums.ArtifactType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ZipEmitter implements ArtifactEmitter{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public boolean supports(CompilationRoot root) {
        return false;
    }

    @Override
    public void emit(CompilationOutcome outcome) {
        if(outcome.isSuccess())
        {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            outcome.getReachableArtifacts().stream().forEach(compiledArtifact -> {
               if(compiledArtifact.type()== ArtifactType.Challenge)
               {
                   CompiledChallenge data = (CompiledChallenge) compiledArtifact.payload();
                   CompilationMetadata metadata = compiledArtifact.metadata();
                   File file = new File("target/"+getFileName(compiledArtifact)+".json");
                   try {
                       objectMapper.writeValue(file, data);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }else  if(compiledArtifact.type()== ArtifactType.Task)
               {
                   CompiledTask data = (CompiledTask) compiledArtifact.payload();
                   CompilationMetadata metadata = compiledArtifact.metadata();
                   File file = new File("target/"+getFileName(compiledArtifact)+".json");
                   try {
                       objectMapper.writeValue(file, data);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }else  if(compiledArtifact.type()== ArtifactType.Ruleset)
               {

                   CompiledRuleset data = (CompiledRuleset) compiledArtifact.payload();
                   CompilationMetadata metadata = compiledArtifact.metadata();
                   File file = new File("target/"+getFileName(compiledArtifact)+".json");
                   try {
                       objectMapper.writeValue(file, data);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }

 


               }else  if(compiledArtifact.type()== ArtifactType.Rule)
               {
                   CompiledRule data = (CompiledRule) compiledArtifact.payload();
                   CompilationMetadata metadata = compiledArtifact.metadata();
                   File file = new File("target/"+getFileName(compiledArtifact)+".json");
                   try {
                       objectMapper.writeValue(file, data);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               } if(compiledArtifact.type()== ArtifactType.EvidenceType)
                {
                    /*
                    CompiledEvidenceType data = (CompiledEvidenceType) compiledArtifact.payload();
                    CompilationMetadata metadata = compiledArtifact.metadata();
                    File file = new File("target/"+getFileName(compiledArtifact)+".json");
                    try {
                        objectMapper.writeValue(file, data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                     */
                }

            });
        }
    }

    public String getFileName(CompiledArtifact compiledArtifact )
    {
        StringBuilder builder = new StringBuilder(
                compiledArtifact.id()
        );
        builder.append("-");
        builder.append(compiledArtifact.version());
        builder.append("-");
        return builder.toString();
    }
}
