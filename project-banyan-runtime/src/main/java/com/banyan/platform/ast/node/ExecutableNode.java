package com.banyan.platform.ast.node;

import com.banyan.platform.runtime.EvidenceContext;

public interface ExecutableNode {
    boolean evaluate(EvidenceContext context);
}
