package com.banyan.compiler.enums;

public enum LogicalOperator {
    AND,
    OR;
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
