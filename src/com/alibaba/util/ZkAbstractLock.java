package com.alibaba.util;

import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

/**
 * 公共部分抽象出来
 */
public abstract class ZkAbstractLock implements Lock {

    private static final String CONNECTION_STRING = "127.0.0.1:2181";

    /**
     * 分布式环境下，每个应用节点都作为一个zk客户端
     */
    protected ZkClient zkClient = new ZkClient(CONNECTION_STRING);

    /**
     * 在zookeeper中创建的节点名称
     */
    protected static final String PATH = "/lock";

    /**
     * 计数器
     */
    protected CountDownLatch countDownLatch;

    @Override
    public void getLock() {
        if (tryLock()) {
            System.out.println("--------获取锁成功--------");
        } else {
            System.out.println("--------等待锁--------");
            waitLock();
            System.out.println("-------等待线程唤醒--------");
            getLock();
        }
    }

    protected abstract void waitLock();

    protected abstract boolean tryLock();

    @Override
    public void unLock() {
        if (zkClient != null) {
            zkClient.close();
        }
        System.out.println("--------释放锁成功--------");
    }
}
