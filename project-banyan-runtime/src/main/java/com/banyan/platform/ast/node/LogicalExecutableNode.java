package com.banyan.platform.ast.node;

import com.banyan.compiler.enums.LogicalOperator;
import com.banyan.platform.runtime.EvidenceContext;
import com.banyan.platform.runtime.darLoader.ZipDarLoader;
import com.banyan.platform.runtime.exception.InvalidEvidenceTypeException;
import com.banyan.platform.runtime.exception.MissingEvidenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
public final class LogicalExecutableNode implements ExecutableNode {

    private final LogicalOperator operator;
    private final List<ExecutableNode> children;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(LogicalExecutableNode.class);
    public LogicalExecutableNode(
            LogicalOperator operator,
            List<ExecutableNode> children
    ) {
        this.operator = operator;
        this.children = children;
    }

    @Override
    public boolean evaluate(EvidenceContext context) {

        try {
            switch (operator) {

                case AND:
                    for (ExecutableNode child : children) {
                        try {
                            if (!child.evaluate(context)) {
                                return false;
                            }
                        }catch(MissingEvidenceException e)
                        {
                            return false;
                        }
                    }
                    return true;

                case OR:
                    for (ExecutableNode child : children)
                    {
                        try {
                            if (child.evaluate(context)) {
                                return true;
                            }
                        }
                        catch(MissingEvidenceException e)
                        {
                            LOGGER.info("Missing evidence {}",e.getMessage());
                        }
                    }
                    return false;

                default:
                    throw new InvalidEvidenceTypeException("Unsupported operator");
            }
        }
        catch(Exception e)
        {
            throw new MissingEvidenceException("Operator not available");
        }
    }

}
