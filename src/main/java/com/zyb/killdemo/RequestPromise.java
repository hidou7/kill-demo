package com.zyb.killdemo;


import lombok.Data;

@Data
public class RequestPromise {

    private UserRequest userRequest;

    private Result result;

    public RequestPromise(UserRequest userRequest) {
        this.userRequest = userRequest;
    }
}
