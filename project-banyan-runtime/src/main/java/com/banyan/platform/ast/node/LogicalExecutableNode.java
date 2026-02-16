package com.banyan.platform.ast.node;
import java.util.List;
public abstract class LogicalExecutableNode
        implements ExecutableNode {

    protected final List<ExecutableNode> children;

    protected LogicalExecutableNode(List<ExecutableNode> children) {
        this.children = children;
    }
}
