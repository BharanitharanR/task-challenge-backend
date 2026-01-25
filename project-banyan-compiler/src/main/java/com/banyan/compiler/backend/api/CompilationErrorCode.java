package com.banyan.compiler.backend.api;


public enum CompilationErrorCode {

    // Dependency errors
    MISSING_DEPENDENCY,
    VERSION_NOT_FOUND,
    TYPE_MISMATCH,

    // Structural errors
    INVALID_GRAPH,
    CYCLIC_DEPENDENCY,

    // Backend failures
    AST_BUILD_FAILED,
    CONTEXT_CORRUPTED,
    // CONTEXT_FREEZE
    CONTEXT_FREEZE,
    // Catch-all
    INTERNAL_COMPILER_ERROR
}
