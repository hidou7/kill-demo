package com.zyb.killdemo;

import lombok.Data;

/**
 * 模拟用户请求
 */
@Data
public class UserRequest {

    private Long orderId;

    private Long userId;

    private Integer count;

    public UserRequest(Long orderId, Long userId, Integer count) {
        this.orderId = orderId;
        this.userId = userId;
        this.count = count;
    }
}
