package scc.serverless.azure.cache;

import java.util.concurrent.CancellationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.serverless.utils.Constants;
import scc.serverless.utils.Session;
public class RedisCache {
    private static final String RedisHostname = Constants.camposConst.getRedisHostname();
    private static final String RedisKey = Constants.camposConst.getredisKey();

    private static final int TTL = 60 * 15;

    private static JedisPool instance;
    private static RedisCache cache;

    public synchronized static JedisPool getCachePool() {
        if (instance != null)
            return instance;
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        instance = new JedisPool(poolConfig, RedisHostname, 6380, 1000, RedisKey, true);
        return instance;
    }

    public synchronized static RedisCache getInstance() {
        if (cache != null)
            return cache;
        cache = new RedisCache();
        return cache;
    }

    public <T> void setValue(String id, T item) {
        ObjectMapper mapper = new ObjectMapper();
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            jedis.set(id, mapper.writeValueAsString(item));
            jedis.expire(id, TTL);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public <T> T getValue(String id, Class<T> type) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String str = jedis.get(id);
            ObjectMapper mapper = new ObjectMapper();
            T item = null;
            try {
                item = mapper.readValue(str, type);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return item;
        }
    }

    public <T> void delete(String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            jedis.del(id);
        }
    }

    public void putSession(Session session) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String cacheId = session.getUid();
            jedis.set(cacheId, session.getUsername());
            jedis.expire(cacheId, TTL);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Session getSession(String uid) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(uid);

            if (res == null)
                throw new CancellationException();

            return new Session(uid, res);
        } catch (Exception e) {
            throw new CancellationException();
        }
    }
}
