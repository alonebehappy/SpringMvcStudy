package com.alibaba.util;
 
import java.util.*;
 
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
 
import com.qlchat.common.util.JedisUtil;
import com.qlchat.common.util.QlchatUtil;
 
/**
 * 默认操作redis库是DEFAULT_REDIS_DB = 1
 * @author zhangk
 *
 */
public class BaseCache extends JedisUtil {
    private static Logger log = LoggerFactory.getLogger(BaseCache.class);
     
    private static Long REDIS_INCR_MAX = 4294967294L;
     
    private static int DEFAULT_REDIS_DB = 0;//默认redis库
 
    /**
     *
     * 设置value
     *
     * @since 1.0.0
     */
    public static void setValue(String key, String value, Integer expire) {
        Jedis jedis = getJedis();
        try {
            // 如果存在则插入覆盖，如果不存在则初始化
            if(expire != null){
                Long ttl = jedis.ttl(key);
                jedis.set(key, value);
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                jedis.set(key, value);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static void setHashFieldValue(String key, String field, String value) {
        setHashFieldValue(key, field, value, null);
    }
 
    /**
     *
     *  更新或新增缓存信息
     *
     * @since 1.0.0
     */
    public static void setHashFieldValue(String key, String field, String value, Integer expire) {
        Jedis jedis = getJedis();
        try {
            if(expire != null){
                Long ttl = jedis.ttl(key);
                jedis.hset(key, field, value);
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                jedis.hset(key, field, value);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
 
    public static void setHashMapValue(String key, Map<String, String> map, Integer expire) {
        setHashMapValue(key, map, DEFAULT_REDIS_DB, expire);
    }
 
    /**
     *
     *  更新或新增缓存信息
     *
     * @exception
     * @since 1.0.0
     */
    public static void setHashMapValue(String key, Map<String, String> map, int db, Integer expire) {
        Jedis jedis = getJedis();
        try {
            if(expire != null){
                Long ttl = jedis.ttl(key);
                jedis.hmset(key, map);
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                jedis.hmset(key, map);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static String getValue(String key){
        return getValue(key, DEFAULT_REDIS_DB);
    }
    public static String getValue(String key, int db) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = getJedis();
        String val = null;
        try {
            val = jedis.get(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val != null ? val : StringUtils.EMPTY;
    }
    public static String getHashFieldValue(String key, String field) {
        return getHashFieldValue(key, field, DEFAULT_REDIS_DB);
    }
 
    /**
     *
     *  获取Value Map 的值
     *
     * @since 1.0.0
     */
    public static String getHashFieldValue(String key, String field, int db) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = getJedis();
        String val = null;
        try {
            val = jedis.hget(key, field);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
 
    /**
     * 获取field是否存在
     * @param key key
     * @param field field
     * @param db db
     * @return boolean
     */
    public static boolean getHashFieldExist(String key, String field, int db) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return false;
        }
        Jedis jedis = getJedis();
        try {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }
 
    public static Map<String, String> getHashAll(String key){
        return getHashAll(key, DEFAULT_REDIS_DB);
    }
 
    public static Map<String, String> getHashAll(String key, int db) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = getJedis();
        Map<String, String> val = null;
        try {
            val = jedis.hgetAll(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val == null ? new HashMap<String, String>() : val;
    }
 
    public static void delHashFiled(String key, String... fields) {
        delHashFiled(key, DEFAULT_REDIS_DB, fields);
    }
 
    /**
     *
     *  删除一个或多个field
     *
     * @since 1.0.0
     */
    public static void delHashFiled(String key, int db, String... fields) {
        if (StringUtils.isBlank(key) || fields == null || fields.length < 1) {
            return;
        }
        Jedis jedis = getJedis();
        try {
            jedis.hdel(key, fields);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
 
    public static void delKey(String... key) {
        delKey(DEFAULT_REDIS_DB, key);
    }
 
    /**
     *
     *  删除一个或多个key
     *
     * @since 1.0.0
     */
    public static void delKey(int db, String... key) {
        if (key == null || key.length < 1) {
            return;
        }
        Jedis jedis = getJedis();
        try {
//          jedis.select(db);
            jedis.del(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
     
    /**
     * 自增
     * @param incr >0表示加  <0表示减
     */
    public static Long hincr(String key, String field, Long incr, Integer expire) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
            if(expire != null){
                Long ttl = jedis.ttl(key);
                val = jedis.hincrBy(key, field, incr);
                if (val >= REDIS_INCR_MAX ) {//Integer无符号的最大数
                    jedis.set(key, "0");
                }
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                val = jedis.hincrBy(key, field, incr);
                if (val >= REDIS_INCR_MAX ) {//Integer无符号的最大数
                    jedis.set(key, "0");
                }
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 自增 1
     * @return 返回操作后的结果
     */
    public static Long incr(String key) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
//          jedis.select(DEFAULT_REDIS_DB);
            val = jedis.incr(key);
            if (val >= REDIS_INCR_MAX ) {//Integer无符号的最大数
                jedis.set(key, "0");
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 增加自定义步长
     */
    public static Long incrBy(String key, Long incr) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
//          jedis.select(DEFAULT_REDIS_DB);
            val = jedis.incrBy(key,incr);
            if (val >= REDIS_INCR_MAX ) {//Integer无符号的最大数
                jedis.set(key, "0");
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
 
    /**
     * 用于做原子锁操作 HSETNX key field value
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效。
     * 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
     * 设置成功，返回 1 。
        如果给定域已经存在且没有操作被执行，返回 0 。
     */
    public static Long setValueIfNotExist(String key, String field, String value) {
        Long val = 0L;
        Jedis jedis = getJedis();
        try {
            int seconds = 3 * 60;//3min
//          jedis.select(RedisLiveKeyConstants.LIVE_TEMP_DB);
            Long ttl = jedis.ttl(key);
            val = jedis.hsetnx(key, field, value);
            if(val > 0){
                jedis.expire(key, seconds);
            }else{//防止设置过期时间失败
                if(ttl != null && (ttl == -1 || ttl > seconds)){
                    //获得的是一个失效的key, 第一次还是失败，第二次就好了
                    jedis.del(key);
                }
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
        假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
        当 key 不是集合类型时，返回一个错误。
     * @param key
     * @param members
     * @return
     */
//  public static Long sadd(String key, Integer expire, String... members) {
//      return sadd(key, DEFAULT_REDIS_DB, expire, members);
//  }
    public static Long saddSet(String key,Integer expire, String... members){
        Long val = null;
        Jedis jedis = getJedis();
        try {
            if (members != null && members.length > 0) {
                if(expire != null){
                    Long ttl = jedis.ttl(key);
                    val = jedis.sadd(key, members);
                    if(ttl != null && ttl > 0){
                        jedis.expire(key, ttl.intValue());
                    }else{
                        jedis.expire(key, expire);
                    }
                }else{
                    val = jedis.sadd(key, members);
                }
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     * @param key
     * @param members
     * @return
     */
    public static Long sdel(String key, String... members) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.srem(key, members);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 判断 member 元素是否集合 key 的成员。
     * true 存在
     * @param key
     * @param member
     * @return
     */
    public static boolean sismember(String key, String member) {
        boolean val = false;
        Jedis jedis = getJedis();
        try {
//          jedis.select(DEFAULT_REDIS_DB);
            val = jedis.sismember(key, member);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 返回集合 key 中的所有成员。不存在的 key 被视为空集合。
     * @param key
     * @return
     */
    public static Set<String> smembers(String key) {
        Set<String> val = null;
        Jedis jedis = getJedis();
        try {
//          jedis.select(DEFAULT_REDIS_DB);
            val = jedis.smembers(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 返回集合随机元素
     * @param key
     * @return
     */
    public static List<String> srandom(String key,int num) {
        List<String> list = new ArrayList<String>();
        Jedis jedis = getJedis();
        try {
            list = jedis.srandmember(key,num);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return list;
    }
    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     * @param key
     * @return
     */
    public static Long slen(String key) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.scard(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 移除并返回集合中的一个随机元素。
     * @param key
     * @return
     */
    public static String spop(String key) {
        String val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.spop(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
     
    /**
     * 检查给定 key 是否存在。
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        boolean val = false;
        Jedis jedis = getJedis();
        try {
            val = jedis.exists(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * @param key
     * @param value
     * @return
     */
    public static Long rpush(String key, Integer db, Integer expire, String... value) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
            if(expire != null){
                Long ttl = jedis.ttl(key);
                val = jedis.rpush(key, value);
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                val = jedis.rpush(key, value);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                  jedis.close();
            }
        }
        return val;
    }
    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最左边)。
     * @param key
     * @param value
     * @return
     */
    public static Long lpush(String key, Integer db, Integer expire, String... value) {
        Long val = null;
        Jedis jedis = getJedis();
        try {
            if(expire != null){
                Long ttl = jedis.ttl(key);
                val = jedis.lpush(key, value);
                if(ttl != null && ttl > 0){
                    jedis.expire(key, ttl.intValue());
                }else{
                    jedis.expire(key, expire);
                }
            }else{
                val = jedis.lpush(key, value);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
 
    /**
     * 列表修剪，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * @param key
     * @param start
     * @param stop
     */
    public static void ltrim(String key, int start, int stop) {
        Jedis jedis = getJedis();
        try {
            jedis.ltrim(key, start, stop);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    /**
     * 将一个或多个值 value 弹出到列表 key 的表尾(最左边)。
     * @param key
     * @param value
     * @return
     */
    public static String rpop(String key, Integer db) {
        String val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.rpop(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * @param key
     * @param db
     * @param start
     * @param end
     * @return
     */
    public static List<String> lrange(String key, Integer db, long start, long end) {
        List<String> val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.lrange(key, start, end);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 返回列表 key 的长度。
     * @param key
     * @param db
     * @return
     */
    public static Long llen(String key, Integer db){
        Long val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.llen(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                  jedis.close();
            }
        }
        return val == null?0:val;
    }
    /**
     * 返回列表 key 的长度。
     * @param key
     * @param db
     * @return
     */
    public static Long hlen(String key){
        Long val = null;
        Jedis jedis = getJedis();
        try {
            val = jedis.hlen(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val == null?0:val;
    }
    /**
     * 设置过期时间
     * @param key
     * @return
     */
    public static Integer ttl(String key){
        if(StringUtils.isBlank(key)) {
            return -2;
        }
         
        Jedis jedis = getJedis();
        Long val = -2L;
        try {
            val  = jedis.ttl(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val.intValue();
    }
    /**
     * HyperLogLog add
     * @param key
     * @return
     */
    public static Integer pfadd(String key, String... elements){
        if(StringUtils.isBlank(key)) {
            return -2;
        }
         
        Jedis jedis = getJedis();
        Long val = -2L;
        try {
            val  = jedis.pfadd(key, elements);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val.intValue();
    }
    /**
     * HyperLogLog count
     * @param key
     * @return
     */
    public static Long pfcount(String key){
        if(StringUtils.isBlank(key)) {
            return 0L;
        }
         
        Jedis jedis = getJedis();
        Long val = 0L;
        try {
            val  = jedis.pfcount(key);
        } catch (JedisException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return val;
    }
    /**
     * 更新直播间最后浏览时间
     * @param userId
     * @param liveId
     * @return
     */
    public static Long zaddLastBrowse(String userId, String liveId, Date date){
        if(QlchatUtil.isEmpty(userId) || QlchatUtil.isEmpty(liveId)){
            return null;
        }
        String cacheKey = "LAST_BROWSE_".concat(userId);
        double score = date.getTime();
        return zadd(cacheKey, liveId, score);
    }
    /**
     * 按照score递减分页获取最新浏览的liveId
     * @param userId
     * @param start
     * @param end
     * @return
     */
    public static Set<String> zrevRangeLastBrowse(String userId, int pageNum, int pageSize){
        if(QlchatUtil.isEmpty(userId)){
            return null;
        }
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize - 1;
        String cacheKey = "LAST_BROWSE_".concat(userId);
        return zrevrange(cacheKey, start, end);
    }
    public static Long zcardLastBrowse(String userId){
        if(QlchatUtil.isEmpty(userId)){
            return null;
        }
        String cacheKey = "LAST_BROWSE_".concat(userId);
        return zcard(cacheKey);
    }
     
    public static Long zremLastBrowse(String userId){
        if(QlchatUtil.isEmpty(userId)){
            return null;
        }
        String cacheKey = "LAST_BROWSE_".concat(userId);
        Long countNum = zcard(cacheKey);
        if(countNum > 60){
            return zremrangeByRank(cacheKey, 0, (countNum.intValue() - 60));
        }
        return null;
    }
}
