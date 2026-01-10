package com.banyan.compiler.enums;

public enum TaskActionsOnly {
    on,
    emit;

    public static boolean contains(String value) {
        try {
            TaskActionsOnly.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
