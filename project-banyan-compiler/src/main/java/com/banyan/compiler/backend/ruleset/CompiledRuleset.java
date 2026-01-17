package com.banyan.compiler.backend.ruleset;

public final class CompiledRuleset {

    private final RulesetExpression root;

    public CompiledRuleset(RulesetExpression root) {
        this.root = root;
    }

    public RulesetExpression root() {
        return root;
    }
}
