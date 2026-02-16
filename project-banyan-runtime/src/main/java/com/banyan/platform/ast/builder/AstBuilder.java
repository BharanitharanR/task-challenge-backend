package com.banyan.platform.ast.builder;

import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.ruleset.LogicalNode;
import com.banyan.compiler.backend.ruleset.RuleRefNode;
import com.banyan.compiler.backend.ruleset.RulesetExpression;
import com.banyan.platform.ast.node.ExecutableNode;
import com.banyan.platform.ast.node.LogicalExecutableNode;
import com.banyan.platform.ast.node.RuleExecutableNode;
import com.banyan.platform.runtime.EvidenceContext;
import com.banyan.platform.runtime.context.DarRuntimeContext;
import com.banyan.platform.runtime.darLoader.ZipDarLoader;
import com.banyan.platform.runtime.exception.BanyanRuntimeException;
import com.banyan.platform.runtime.exception.InvalidEvidenceTypeException;
import com.banyan.platform.runtime.exception.MissingEvidenceException;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class AstBuilder {

    private final DarRuntimeContext context;

    public AstBuilder(
            DarRuntimeContext context
    ) {
        this.context = context;
    }

    public ExecutableNode build(
            DarRuntimeContext.RulesetKey key
    ) {

        RulesetExpression root =
                context.ruleset(key).root();

        return buildNode(root);
    }

    private ExecutableNode buildNode(
            RulesetExpression expr
    ) {

        if (expr instanceof LogicalNode logical) {

            List<ExecutableNode> children =
                    logical.operands()
                            .stream()
                            .map(this::buildNode)
                            .toList();

            return new LogicalExecutableNode(
                    logical.operator(),
                    children
            );
        }

        if (expr instanceof RuleRefNode ref) {

            var ruleKey =
                    new DarRuntimeContext.RuleKey(
                            ref.version(),
                            ref.ruleId()
                    );

            CompiledRule rule =
                    context.rule(ruleKey);
            return new RuleExecutableNode(rule);
        }

        throw new IllegalStateException("Unknown node");
    }

    public static void main(String[] args) throws Exception {

        try {
            // 1. Load DAR
            DarRuntimeContext context =
                    ZipDarLoader.load("/Users/bharani/Documents/task-challenge-backend/project-banyan-runtime/src/main/resources/compilation_package.dar");
            // 2. Pick the only ruleset
            DarRuntimeContext.RulesetKey key = null;

            for (var entry : context.rulesets()) {
                key = entry.getKey();
                break;
            }

            // 3. Build AST
            AstBuilder builder = new AstBuilder(context);
            ExecutableNode root = builder.build(key);

            // 4. Create evidence
            Map<String, Object> evidence = Map.of(
                    "faileAttempts", 4,
                    "score", 75,
                    "userType", 2,
                    "country", "IN",
                    "businessHours", true
            );

            EvidenceContext evidenceContext =
                    new EvidenceContext(evidence);

            // 5. Evaluate
            boolean result = root.evaluate(evidenceContext);

            System.out.println("Evaluation result = " + result);
        }
        catch(MissingEvidenceException | InvalidEvidenceTypeException  e)
        {
            System.out.println("Exception result = " + e.getMessage());
        }
    }

}
