package com.banyan.platform.runtime.darLoader;


import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.banyan.compiler.backend.challenge.CompiledChallenge;
import com.banyan.compiler.backend.challenge.CompiledChallengeArtifact;
import com.banyan.compiler.backend.evidence.CompiledEvidenceType;
import com.banyan.compiler.backend.evidence.CompiledEvidenceTypeArtifact;
import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.rule.CompiledRuleArtifact;
import com.banyan.compiler.backend.ruleset.CompiledRuleset;
import com.banyan.compiler.backend.ruleset.CompiledRulesetArtifact;
import com.banyan.compiler.backend.task.CompiledTask;

import com.banyan.compiler.backend.task.CompiledTaskArtifact;
import com.banyan.platform.deserializer.*;

import com.banyan.platform.runtime.context.DarRuntimeContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZipDarLoader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ZipDarLoader.class);
    private static  final ObjectMapper mapper = new ObjectMapper();

    private static  final Map<DarRuntimeContext.ChallengeKey, CompiledChallenge> challenges = new HashMap<>();
    private static  final Map<DarRuntimeContext.TaskKey, CompiledTask> tasks = new HashMap<>();
    private static  final Map<DarRuntimeContext.RulesetKey, CompiledRuleset> rulesets = new HashMap<>();
    private static  final Map<DarRuntimeContext.RuleKey, CompiledRule> rules = new HashMap<>();
    private static  final Map<DarRuntimeContext.EvidenceTypeKey, CompiledEvidenceType> evidenceTypes = new HashMap<>();

    private static  final CompiledChallengeArtifactDeserializer CHALLENGE =
            new CompiledChallengeArtifactDeserializer(mapper);
    private static  final CompiledTaskArtifactDeserializer TASK =
            new CompiledTaskArtifactDeserializer(mapper);
    private static  final CompiledRulesetArtifactDeserializer RULESET =
            new CompiledRulesetArtifactDeserializer(mapper);
    private static  final CompiledRuleArtifactDeserializer RULE =
            new CompiledRuleArtifactDeserializer(mapper);
    private static  final CompiledEvidenceTypeArtifactDeserializer EVIDENCETYPE =
            new CompiledEvidenceTypeArtifactDeserializer(mapper);

    public static DarRuntimeContext load(String darPath) throws Exception {

        try (ZipFile zip = new ZipFile(darPath)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                InputStream in = zip.getInputStream(entry);
                if (name.startsWith("Challenge/")) {
                    LOGGER.info(name);
                    JsonNode names = mapper.readTree(in);
                    CompiledChallengeArtifact artifact = CHALLENGE.deserialize(names);
                    challenges.put(new DarRuntimeContext.ChallengeKey(artifact.version(),artifact.id()),artifact.payload());
                } else if (name.startsWith("Task/")) {
                    LOGGER.info(name);
                    JsonNode names = mapper.readTree(in);
                    CompiledTaskArtifact artifact = TASK.deserialize(names);
                    tasks.put(new DarRuntimeContext.TaskKey(artifact.version(), artifact.id()),artifact.payload());

                } else if (name.startsWith("Ruleset/")) {
                    LOGGER.info(name);
                    JsonNode names = mapper.readTree(in);
                    CompiledRulesetArtifact artifact = RULESET.deserialize(names);
                    artifact.dependencies().forEach(task->{System.out.println(task.id()+task.type()+task.version());});
                    rulesets.put(new DarRuntimeContext.RulesetKey(artifact.version(), artifact.id()),artifact.payload());
                } else if (name.startsWith("Rule/")) {
                    LOGGER.info(name);
                    JsonNode names = mapper.readTree(in);
                    CompiledRuleArtifact artifact = RULE.deserialize(names);
                    rules.put(new DarRuntimeContext.RuleKey(artifact.version(), artifact.id()),artifact.payload());
                } else if (name.startsWith("EvidenceType/")) {
                    LOGGER.info(name);
                    JsonNode names = mapper.readTree(in);
                    CompiledEvidenceTypeArtifact artifact = EVIDENCETYPE.deserialize(names);
                    evidenceTypes.put(new DarRuntimeContext.EvidenceTypeKey(artifact.version(), artifact.id()),artifact.payload());
                }
            }
            return new DarRuntimeContext(challenges,tasks,rulesets,rules,evidenceTypes);
        }
    }

    public static void main(String args[]) throws Exception {
        DarRuntimeContext runtimeContext = ZipDarLoader.load("/Users/bharani/Documents/task-challenge-backend/project-banyan-runtime/src/main/resources/compilation_package.dar");
        for (var entry : runtimeContext.rulesets()) {
            DarRuntimeContext.RulesetKey key = entry.getKey();
            CompiledRuleset ruleset = entry.getValue();
        }
    }
}
