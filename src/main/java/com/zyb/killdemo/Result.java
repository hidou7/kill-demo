package com.zyb.killdemo;

import lombok.Data;

@Data
public class Result {

    private Boolean success;

    private String message;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
