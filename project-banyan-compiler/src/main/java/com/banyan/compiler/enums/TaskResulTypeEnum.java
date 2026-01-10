package com.banyan.compiler.enums;

public enum TaskResulTypeEnum {
    BOOLEAN,NUMBER,SCORE, DURATION;


    public static boolean contains(String value)
    {
        try {
            TaskResulTypeEnum.valueOf(value);
            return true;
        }
        catch(IllegalArgumentException e)
        {
            return false;
        }
    }
}
