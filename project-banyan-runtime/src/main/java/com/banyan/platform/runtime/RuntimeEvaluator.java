package com.banyan.platform.runtime;

import com.banyan.platform.challenge.CompiledChallenge;

import java.util.Map;

public final class RuntimeEvaluator {
    private final ChallengeAstRegistry challengeAstRegistry;
    public RuntimeEvaluator(ChallengeAstRegistry challengeAstRegistry)
    {
        this.challengeAstRegistry = challengeAstRegistry;
    }

    public Map<String, Boolean> evaluate(
            String challengeId,
            int version,
            int failedAttempts,
            boolean businessHours
    )
    {
        //EvidenceContext ctx = new EvidenceContext(failedAttempts,businessHours);
        CompiledChallenge challenge = this.challengeAstRegistry.get(challengeId,version);
        return null;//challenge.evaluate(ctx);
    }
}
