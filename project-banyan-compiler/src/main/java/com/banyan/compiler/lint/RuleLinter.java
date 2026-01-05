package com.banyan.compiler.lint;

import java.util.List;

public class RuleLinter implements Linter{
    @Override
    public List<LintFinding> lint(String json) {
        return List.of();
    }
}
