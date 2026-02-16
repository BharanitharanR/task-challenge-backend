package com.banyan.platform.ast.node;

import com.banyan.platform.runtime.EvidenceContext;
import com.banyan.platform.runtime.exception.MissingEvidenceException;

import java.util.List;

public final class AndLogicalNode
        extends LogicalExecutableNode {

    public AndLogicalNode(List<ExecutableNode> children) {
        super(children);
    }

    @Override
    public boolean evaluate(EvidenceContext context) {

        for (ExecutableNode child : children)
        {
            try {
                if (!child.evaluate(context)) {
                    return false; // short-circuit
                }
            }
            catch(MissingEvidenceException e)
            {
                return false;
            }
        }
        return true;
    }
}

