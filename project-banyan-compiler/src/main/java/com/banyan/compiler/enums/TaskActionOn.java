package com.banyan.compiler.enums;

public enum TaskActionOn {
    SUCCESS,FAILURE,ALWAYS;
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
