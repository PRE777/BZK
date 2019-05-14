package com.iwhere.gridgeneration.comm.enums;

public enum ProcessFlagEnum {

    SUCCESS("00"), FAIL("01");

    private String value;

    public String value(){
        return this.value;
    }

    ProcessFlagEnum(String value){
        this.value = value;
    }

    public static ProcessFlagEnum getEnum(String value) {
        for (ProcessFlagEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }

    public boolean equals(ProcessFlagEnum type){
        return this.value.equals(type.value);
    }
}
