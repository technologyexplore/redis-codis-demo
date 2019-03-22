package com.itmuch.cloud.study.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itmuch.cloud.study.entity.Grade;

import redis.clients.jedis.Jedis;

@Repository
public class RedisService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	public void add(String key, Grade grade, Long time) {
		Gson gson = new Gson();
		stringRedisTemplate.opsForValue().set(key, gson.toJson(grade), time, TimeUnit.MINUTES);

	}

	public void add(String key, List<Grade> grades, Long time) {
		Gson gson = new Gson();
		String src = gson.toJson(grades);
		stringRedisTemplate.opsForValue().set(key, src, time, TimeUnit.MINUTES);
	}

	public Grade get(String key) {
		String source = stringRedisTemplate.opsForValue().get(key);
		if (!StringUtils.isEmpty(source)) {
			return new Gson().fromJson(source, Grade.class);
		}
		return null;
	}

	public List<Grade> getUserList(String key) {
		String source = stringRedisTemplate.opsForValue().get(key);
		if (!StringUtils.isEmpty(source)) {
			return new Gson().fromJson(source, new TypeToken<List<Grade>>() {
			}.getType());
		}
		return null;
	}

	private final AtomicInteger tick = new AtomicInteger();

	/**
	 * 判断是否超出处理范围
	 * 
	 * @return
	 */
	private boolean waitInLine() {
		return tick.incrementAndGet() > 10;
	}

	private static String FLOW_FILTER = "flow_filter";
	private static String MERCHANDISE = "merchandise";
	private static String RANKLIST = "ranklist";

	private static String SET_1 = "set_1";
	private static String SET_2 = "set_2";
	private static String SET_JIAOJI_RESULT = "set_jiaoji_result";
	private static String SET_BINGJI_RESULT = "set_bingji_result";
	private static String SET_CHAJI_RESULT = "set_chaji_result";

	private static String KEY7 = "key7";
	private static String KEY8 = "key8";
	private static String KEY9 = "key9";

	/**
	 * 秒杀设计 (商品10件,每个用户抢购一次,抢完即止.)
	 * 
	 * @param id
	 * @return
	 */
	public String flowControl() {
		String id = UUID.randomUUID().toString().replace("-", "").toLowerCase();

		if (waitInLine()) {
			return "抢购人太多，请稍候";
		}
		try {
			// 查看在售商品是否卖完
			// 对每个请求在KEY1的HASH表以id为field加一
			// 加一后返回结果是否等于1,等于1意味着首次到来
			if (stringRedisTemplate.opsForList().size(MERCHANDISE) != 0
					&& stringRedisTemplate.opsForHash().increment(FLOW_FILTER, id.toString(), 1) == 1) {
				// 从奖品队列获取奖品
				Object obj = stringRedisTemplate.opsForList().leftPop(MERCHANDISE);
				if (obj == null) {
					return "商品售卖完毕";
				}
				System.out.println("用户id:" + id + " ---- 商品名称:" + obj);
				return "用户id:" + id + " ---- 商品名称:" + obj;
			}
			return "抢购人太多，请稍候";
		} catch (Exception e) {
			e.printStackTrace();
			return "系统异常";
		} finally {
			tick.decrementAndGet();
		}
	}

	public void initializationRedisRanklistData() {
		ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
		stringRedisTemplate.delete(RANKLIST);
		for (int i = 1; i < 11; i++) {
			zset.add(RANKLIST, "a" + i, i);
		}
	}

	/**
	 * 排名
	 * 
	 * @param id
	 * @return
	 */
	public String rankingList() {
		Set<TypedTuple<String>> ss;
		Set<String> s1;
		ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
		try {
			ss = zset.rangeWithScores(RANKLIST, 0, -1);// 按照score小到大排序,有member有score
			System.out.println("===============顺序===============");
			for (TypedTuple<String> s : ss) {
				System.out.println("数字:" + s.getScore() + "   内容:" + s.getValue());
			}
			// s1 = zset.range(RANKLIST, 0, -1);// 按照score小到大排序,获取member

			System.out.println("===============倒叙===============");
			ss = zset.reverseRangeWithScores(RANKLIST, 0, -1);// 按照score大到小排序,有member有score
			for (TypedTuple<String> s : ss) {
				System.out.println("数字:" + s.getScore() + "   内容:" + s.getValue());
			}
			// s1 = zset.reverseRange(RANKLIST, 0, -1);// 按照score大到小排序,获取member

			// zset.count(RANKLIST, 1, 2);// 计算范围内有多少个member
			// zset.zCard(RANKLIST);// 计算有多少个member
			//
			// zset.score(RANKLIST, "b");// 获取某一member的score

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 初始化数据
	 */
	public void initializationRedisSetData() {
		stringRedisTemplate.delete(SET_1);
		stringRedisTemplate.delete(SET_2);
		stringRedisTemplate.delete(SET_JIAOJI_RESULT);
		stringRedisTemplate.delete(SET_BINGJI_RESULT);
		stringRedisTemplate.delete(SET_CHAJI_RESULT);
		SetOperations<String, String> sets = stringRedisTemplate.opsForSet();
		for (int i = 6; i < 11; i++) {
			sets.add(SET_1, String.valueOf(i));
		}
		for (int i = 6; i < 16; i++) {
			sets.add(SET_2, String.valueOf(i));
		}
	}

	public void setHandel() {
		SetOperations<String, String> sets = stringRedisTemplate.opsForSet();
		sets.intersectAndStore(SET_1, SET_2, SET_JIAOJI_RESULT);// 交集 比较值1 比较值2 结果
		sets.unionAndStore(SET_1, SET_2, SET_BINGJI_RESULT);// 并集 比较值1 比较值2 结果
		sets.differenceAndStore(SET_1, SET_2, SET_CHAJI_RESULT);// 差集 比较值1 比较值2 结果
	}

	public void getLockOld(final String key) {
		String value = UUID.randomUUID().toString();
		String status = stringRedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				Jedis jedis = (Jedis) connection.getNativeConnection();
				//
				if (jedis.setnx(key, value) == 1) {
					// 若在这里程序突然崩溃，则无法设置过期时间，将发生死锁
					System.out.println("redis key=" + key + " lock!");
					return jedis.expire(key, 60).toString();
				}
				return null;
			}
		});
	}

	public boolean getLockNew(final String key) {
		final String value = UUID.randomUUID().toString();
		String status = stringRedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				Jedis jedis = (Jedis) connection.getNativeConnection();
				System.out.println("redis key=" + key + " lock!");
				return jedis.set(key, value, "nx", "ex", 60);
			}
		});
		return "OK".equals(status);
	}

	public void unlockOld(String lockKey, String lockValue) {
		// 获取redis中设置的时间
		String result = stringRedisTemplate.opsForValue().get(lockKey);
		// 如果是加锁者，则删除锁， 如果不是，则等待自动过期，重新竞争加锁
		if (result != null && result.equals(lockValue)) {
			// 若在这里程序突然崩溃，则无法删除，则等待自动过期
			stringRedisTemplate.delete(lockKey);
		}
	}

	public void unlockNew(String lockKey, String lockValue) {
		// 获取redis中设置的时间
		String status = stringRedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				Jedis jedis = (Jedis) connection.getNativeConnection();
				String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
				Object result = jedis.eval(script, Collections.singletonList(lockKey),
						Collections.singletonList(lockValue));

				return null;
			}
		});
	}

	/**
	 * 初始化数据
	 */
	public void initializationRedisFlowData() {
		stringRedisTemplate.delete(FLOW_FILTER);
		stringRedisTemplate.delete(MERCHANDISE);
		for (int i = 0; i < 10; i++) {
			stringRedisTemplate.opsForList().leftPush(MERCHANDISE, "奖品" + i);
		}
	}

	public boolean wrongGetLock2(String lockKey, int expireTime) {

		long expires = System.currentTimeMillis() + expireTime;
		String expiresStr = String.valueOf(expires);

		String status = stringRedisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				Jedis jedis = (Jedis) connection.getNativeConnection();
				if (jedis.setnx(lockKey, expiresStr) == 1) {
					return "0";
				}

				// 如果锁存在，获取锁的过期时间
				String currentValueStr = jedis.get(lockKey);
				if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
					// 锁已过期，获取上一个锁的过期时间，并设置现在锁的过期时间
					String oldValueStr = jedis.getSet(lockKey, expiresStr);
					if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
						// 考虑多线程并发的情况，只有一个线程的设置值和当前值相同，它才有权利加锁
						return "0";
					}
				}

				// 其他情况，一律返回加锁失败
				return "1";
			}
		});
		// 如果当前锁不存在，返回加锁成功
		return false;
	}

	public void setString() {
		String value = UUID.randomUUID().toString();
		ValueOperations<String, String> s = stringRedisTemplate.opsForValue();
		s.set(value, "123abc");
	}

	
	public void setString1() {
		System.out.println(stringRedisTemplate.opsForHash().putIfAbsent("klo", "aaa", ""));
	}
}