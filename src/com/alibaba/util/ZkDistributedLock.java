package com.alibaba.util;

import com.zpc.zklock.Lock;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 使用zookeeper实现分布式锁
 * 上锁思路：创建临时有序节点，每个上锁的节点均能创建节点成功，只是其序号不同。只有序号最小的可以拥有锁，如果这个节点序号不是最小的则 watch 序号比本身小的前一个节点 (公平有序)。
 * 步骤：
 * 在zookeeper的/lock节点下创建一个有序临时节点(EPHEMERAL_SEQUENTIAL)。
 * 判断当前客户端创建的节点序号是否最小，如果是最小则该客户端获取锁成功。不是则取锁失败，然后 watch 序号比本身小的前一个节点。
 * 当取锁失败，设置 watch 后则等待 watch 事件到来后，再次判断是否序号最小。
 * 取锁成功则执行代码，最后释放锁（删除该节点）。
 */
public class ZkDistributedLock implements Lock, Watcher {
    private ZooKeeper zk;
    private String root = "/lock";//根
    private String lockName;//竞争资源的标志,lockName中不能包含单词lock
    private String waitNode;//等待前一个锁
    private String myZnode;//当前锁
    private CountDownLatch latch;//计数器
    private int sessionTimeout = 12000;
    private List<Exception> exception = new ArrayList<Exception>();

    public ZkDistributedLock(String config, String lockName) {
        this.lockName = lockName;
        // 创建一个与服务器的连接
        try {
            zk = new ZooKeeper(config, sessionTimeout, this);
            Stat stat = zk.exists(root, false);
            if (stat == null) {
                // 创建根节点(第一个连上服务器的客户端负责创建)
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            exception.add(e);
        } catch (KeeperException e) {
            e.printStackTrace();
            exception.add(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            exception.add(e);
        }
    }


    @Override
    public void getLock() {
        if (exception.size() > 0) {
            System.out.println("=======exceptions==========" + exception);
            throw new LockException(exception.get(0));
        }
        try {
            if (this.tryLock()) {
                System.out.println("Thread-" + Thread.currentThread().getId() + "-" + myZnode + " 获取锁成功");
                return;
            } else {
                //等待锁
                waitForLock(waitNode, sessionTimeout);
            }
        } catch (KeeperException e) {
            throw new LockException(e);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
    }

    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (lockName.contains(splitStr)) {
                throw new LockException("lockName can not contains the word 'lock'");
            }
            //创建临时子节点
            myZnode = zk.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("=======================" + myZnode + " is created================== ");
            //取出所有子节点
            List<String> subNodes = zk.getChildren(root, false);
            //取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<String>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjNodes.add(node);
                }
            }
            System.out.println("排序前=====================" + lockObjNodes);
            Collections.sort(lockObjNodes);
            System.out.println("排序后=====================" + lockObjNodes);
            System.out.println(myZnode + "==" + lockObjNodes.get(0));
            if (myZnode.equals(root + "/" + lockObjNodes.get(0))) {
                //如果是最小的节点,则表示取得锁
                System.out.println("--------获取锁成功--------");
                return true;
            }
            //如果不是最小的节点，找到比自己小1的节点
            String myNodeSerialNum = myZnode.substring(myZnode.lastIndexOf("/") + 1);
            System.out.println("myZnode.lastIndexOf=====" + myZnode.lastIndexOf("/"));
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, myNodeSerialNum) - 1);
        } catch (KeeperException e) {
            throw new LockException(e);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
        return false;
    }

    private boolean waitForLock(String waitNode, long waitTime) throws InterruptedException, KeeperException {
        //判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
        Stat stat = zk.exists(root + "/" + waitNode, true);
        if (stat != null) {
            System.out.println("Thread-" + Thread.currentThread().getId() + " waiting for " + root + "/" + waitNode);
            this.latch = new CountDownLatch(1);
            //阻塞线程，直到latch=0
            this.latch.await(waitTime, TimeUnit.MILLISECONDS);
            this.latch = null;
        }
        return true;
    }

    @Override
    public void unLock() {
        try {
            System.out.println("unlock " + myZnode);
            zk.delete(myZnode, -1);
            myZnode = null;
            if (zk != null) {
                zk.close();
            }
            System.out.println("--------释放锁成功--------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent event) {
        if (this.latch != null) {
            this.latch.countDown();
        }
    }

    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LockException(String e) {
            super(e);
        }

        public LockException(Exception e) {
            super(e);
        }
    }
}
