package com.banyan.platform.runtime.exception;

public final class MissingEvidenceException
        extends BanyanRuntimeException {

    private final String field;

    public MissingEvidenceException(String field) {
        super("Missing evidence field: " + field);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
