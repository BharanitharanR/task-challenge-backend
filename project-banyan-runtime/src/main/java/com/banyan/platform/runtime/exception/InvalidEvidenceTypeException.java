package com.banyan.platform.runtime.exception;

public class InvalidEvidenceTypeException extends BanyanRuntimeException {

    private final String field;
    public InvalidEvidenceTypeException(String message) {
        super("Missing evidence field: " + message);
        this.field = message;
    }

    public String field() {
        return field;
    }
}