package com.zpc.zklock;

/**
 * 面向接口编程
 */
public interface Lock {
    /**
     * 获取锁
     */
    public void getLock();

    /**
     * 释放锁
     */
    public void unLock();
}
