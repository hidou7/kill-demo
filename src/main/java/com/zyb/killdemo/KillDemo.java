package com.zyb.killdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class KillDemo {

    private int stock = 6;

    private final BlockingDeque<RequestPromise> queue = new LinkedBlockingDeque<>(6);

    public Result order(UserRequest userRequest) {
        RequestPromise promise = new RequestPromise(userRequest);
        // 为什么需要synchronized？
        //  1、promise有竞争；
        //  2、wait/notify都必须在同步代码块里才能调用
        synchronized (promise){
            boolean offer = false;
            try {
                // 加入队列
                offer = queue.offer(promise, 100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!offer){
                return new Result(false, "系统繁忙");
            }
            try {
                promise.wait(200);
                if(promise.getResult() == null){
                    return new Result(false, "等待超时");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return promise.getResult();
    }

    /**
     * 监听用户请求处理
     */
    public void mergeJob(){
        new Thread(()->{
            List<RequestPromise> list = new ArrayList<>();
            while (true){
                if(queue.isEmpty()){
                    try{
                        Thread.sleep(10);
                        continue;
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    list.add(queue.poll());
                }
                // 合并减库存
                int sum = list.stream().mapToInt(e -> e.getUserRequest().getCount()).sum();
                if(sum <= stock){
                    stock -= sum;
                    for (RequestPromise promise : list) {
                        promise.setResult(new Result(true, "ok"));
                        synchronized (promise){
                            promise.notify();
                        }
                    }
                }else{
                    for (RequestPromise promise : list) {
                        int count = promise.getUserRequest().getCount();
                        if(count <= stock){
                            stock -= count;
                            promise.setResult(new Result(true, "ok"));
                        }else{
                            promise.setResult(new Result(false, "库存不足"));
                        }
                        synchronized (promise){
                            promise.notify();
                        }
                    }
                }
                list.clear();
            }
        }).start();
    }
}
