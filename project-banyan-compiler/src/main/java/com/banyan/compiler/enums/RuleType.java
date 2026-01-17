package com.banyan.compiler.enums;

public enum RuleType {
 THRESHOLD,EQUALITY,RANGE;
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
