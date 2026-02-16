package com.banyan.platform.ast.builder;

import com.banyan.compiler.backend.rule.CompiledRule;
import com.banyan.compiler.backend.ruleset.LogicalNode;
import com.banyan.compiler.backend.ruleset.RuleRefNode;
import com.banyan.compiler.backend.ruleset.RulesetExpression;
import com.banyan.platform.ast.node.ExecutableNode;
import com.banyan.platform.ast.node.LogicalExecutableNode;
import com.banyan.platform.ast.node.RuleExecutableNode;
import com.banyan.platform.runtime.context.DarRuntimeContext;
import java.util.List;
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

}
