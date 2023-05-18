package com.zyb.killdemo;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        KillDemo killDemo = new KillDemo();
        // 启动监听用户请求的处理
        killDemo.mergeJob();
        Thread.sleep(1500);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        // 模拟10个用户并发请求
        for (int i = 0; i < 10; i++) {
            long orderId = i + 100L;
            long userId = i;
            CompletableFuture.supplyAsync(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await(1000, TimeUnit.SECONDS);
                    return killDemo.order(new UserRequest(orderId, userId, 1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }, executorService)
            .whenComplete((result, throwable) -> System.out.println(Thread.currentThread().getName() + " " + result));
        }
    }
}
