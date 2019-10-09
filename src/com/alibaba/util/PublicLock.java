package com.alibaba.util;
 
import com.qlchat.common.constants.CommonConstants;
import com.qlchat.common.constants.RedisLiveKeyConstants;
import com.qlchat.common.helper.*;
import com.qlchat.component.redis.template.*;
 
public class PublicLock {
    private static final Long TOTAL_WAIT_SENCODS = 10 * 1000L;// 10s获取不到算超时
    private static final Integer REPLY_WAIT_SENCODS = 5;// 重试获取锁间隔时间ms
 
    public static ValueRedisTemplate valueRedisTemplate;
    public static HashRedisTemplate hashRedisTemplate;
    public static ListRedisTemplate listRedisTemplate;
    public static SetRedisTemplate setRedisTemplate;
    public static HyperLogLogTemplate hyperLogLogTemplate;
 
    static {
        valueRedisTemplate = SpringHelper.getBean(ValueRedisTemplate.class);
        hashRedisTemplate = SpringHelper.getBean(HashRedisTemplate.class);
        listRedisTemplate = SpringHelper.getBean(ListRedisTemplate.class);
        setRedisTemplate = SpringHelper.getBean(SetRedisTemplate.class);
        hyperLogLogTemplate = SpringHelper.getBean(HyperLogLogTemplate.class);
    }
 
     
     /**
     * 获取操作锁
     * 需要写线程去过时历史失败的锁
     * @return true 获取成功，false获取失败
     * @throws InterruptedException
     */
    public static boolean getLock(String lockKey, Long waitSencods) throws InterruptedException{
        long t1 = System.currentTimeMillis();
        boolean isSuccess = false;
        while(!isSuccess){
            if (System.currentTimeMillis() <= t1 + waitSencods) {
                isSuccess = tryLock(lockKey, lockKey);
                if(!isSuccess){
                    Thread.sleep(REPLY_WAIT_SENCODS);//sleep10ms
                }
            }else{//超过等待时间，返回失败
                break;
            }
        }
        return isSuccess;
    }
     
    /**
     * 获取操作锁
     * @return true 获取成功，false获取失败
     * @throws InterruptedException
     */
    public static boolean getLock(String lockKey) throws InterruptedException{
        Long waitSencods = TOTAL_WAIT_SENCODS;
        return getLock(lockKey, waitSencods);
    }
    /**
     * 释放锁
     */
    public static void freeLock(String lockKey){
        BaseCache.delKey(RedisLiveKeyConstants.LIVE_TEMP_DB, lockKey);
    }
 
    /**
     * 锁是否存在
     * @param lockKey
     * @return
     */
    public static boolean isLock(String lockKey) {
        return BaseCache.getHashFieldExist(lockKey, lockKey, 0);
    }
    /**
     * 尝试获取锁
     * @return
     */
    private static boolean tryLock(String lockKey, String field){
        boolean isSuccess = false;
        long setValue = BaseCache.setValueIfNotExist(lockKey, field, CommonConstants.YesOrNo.YES);//返回1则成功
        if(setValue > 0){
            isSuccess = true;
        }
        return isSuccess;
    }
}
