package com.banyan.platform.runtime.exception;

public abstract class BanyanRuntimeException
        extends RuntimeException {

    public BanyanRuntimeException(String message) {
        super(message);
    }
}
