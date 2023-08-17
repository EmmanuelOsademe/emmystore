package com.emmydev.ecommerce.client.enums;


public enum ResponseCodes {
    SUCCESS(0, "Success"),
    ERROR(-1, "Error"),
    INVALID_INPUT(-2, "Invalid input");

    private int code;
    private String description;

    ResponseCodes(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode(){return this.code;}

    public String getDescription(){
        return this.description;
    }

}
