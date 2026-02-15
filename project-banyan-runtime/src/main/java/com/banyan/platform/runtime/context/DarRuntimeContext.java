package com.banyan.platform.runtime.context;

import com.banyan.compiler.backend.challenge.CompiledChallenge;
import com.banyan.compiler.backend.evidence.CompiledEvidenceType;
import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.ruleset.CompiledRuleset;
import com.banyan.compiler.backend.task.CompiledTask;
import com.banyan.platform.runtime.darLoader.ZipDarLoader;

import java.util.Map;

public final class DarRuntimeContext {

    private final Map<ChallengeKey, CompiledChallenge> challenges;
    private final Map<TaskKey, CompiledTask> tasks;
    private final Map<RulesetKey, CompiledRuleset> rulesets;
    private final Map<RuleKey, CompiledRule> rules;
    private final Map<EvidenceTypeKey, CompiledEvidenceType> evidenceTypes;


    public record ChallengeKey(int version, String name) {
    }

    public record TaskKey(int version, String name) {
    }

    public record RulesetKey(int version, String name) {
    }

    public record RuleKey(int version, String name) {
    }

    public record EvidenceTypeKey(int version, String name) {
    }

    public DarRuntimeContext(
            Map<ChallengeKey, CompiledChallenge> challenges,
            Map<TaskKey, CompiledTask> tasks,
            Map<RulesetKey, CompiledRuleset> rulesets,
            Map<RuleKey, CompiledRule> rules,
            Map<EvidenceTypeKey, CompiledEvidenceType> evidenceTypes
    ) {
        this.challenges = challenges;
        this.tasks = tasks;
        this.rulesets = rulesets;
        this.rules = rules;
        this.evidenceTypes = evidenceTypes;
    }

    public CompiledChallenge challenge(ChallengeKey key) {
        return require(challenges, key, "Challenge");
    }

    public CompiledTask task(TaskKey key) {
        return require(tasks, key, "Task");
    }

    public CompiledRuleset ruleset(RulesetKey key) {
        return require(rulesets, key, "Ruleset");
    }

    public CompiledRule rule(RuleKey key) {
        return require(rules, key, "Rule");
    }

    public CompiledEvidenceType evidenceType(EvidenceTypeKey key) {
        return require(evidenceTypes, key, "EvidenceType");
    }

    private static <K, V> V require(Map<K, V> map, K key, String type) {
        V value = map.get(key);
        if (value == null) {
            throw new IllegalStateException(
                    type + " not found: " + key
            );
        }
        return value;
    }

    public Iterable<Map.Entry<ChallengeKey, CompiledChallenge>> challenges() {
        return challenges.entrySet();
    }

    public Iterable<Map.Entry<TaskKey, CompiledTask>> tasks() {
        return tasks.entrySet();
    }

    public Iterable<Map.Entry<RulesetKey, CompiledRuleset>> rulesets() {
        return rulesets.entrySet();
    }

    public Iterable<Map.Entry<RuleKey, CompiledRule>> rules() {
        return rules.entrySet();
    }

    public Iterable<Map.Entry<EvidenceTypeKey, CompiledEvidenceType>> evidenceTypes() {
        return evidenceTypes.entrySet();
    }

}
