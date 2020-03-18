package com.damon;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageCallBack {

    private Lock lock = new ReentrantLock();

    private Condition finish = lock.newCondition();

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    public MessageCallBack(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public Object start() throws Exception {

        try {
            lock.lock();

            finish.await(10000, TimeUnit.MILLISECONDS);

            if (this.rpcRequest != null) {
                return this.rpcResponse.getData();
            } else {
                return null;
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * get response
     */
    public void over(RpcResponse rpcResponse) {
        try {
            lock.lock();
            this.rpcResponse = rpcResponse;
            this.finish.signal();
        } finally {
            lock.unlock();
        }
    }


}
