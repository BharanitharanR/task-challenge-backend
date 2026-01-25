package com.banyan.compiler.enums;

public enum CompilationState {
    RUNNING,FAILED,COMPLETED;
    public static boolean contains(String value)
    {
        try {
            TaskActionOn.valueOf(value);
            return true;
        }
        catch(IllegalArgumentException e)
        {
            return false;
        }
    }
}
