package org.aiot.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 线程安全的两级缓存，支持 TTL 过期与自动清理。
 *
 * 默认行为：
 * - 未指定 TTL 时：固定 1 小时（3600_000 ms）
 * - 访问不延长 TTL（固定过期，非滑动窗口）—— 更可预测
 * - 后台每 60 秒清理一次过期项
 */
public class Cache {

	private static final String ROOT_KEY = "root";
	private static final long DEFAULT_TTL_MS = 60 * 60 * 1000L; // 1 hour
	private static final long CLEANUP_INTERVAL_MS = 60_000L; // 60s

	// Concurrent safe: ConcurrentHashMap for key -> subKey map
	private static final Map<Object, Map<Object, CacheData>> CACHE = new ConcurrentHashMap<>();

	// Background cleaner
	static {
		ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "Cache-Cleaner");
			t.setDaemon(true);
			return t;
		});
		cleaner.scheduleAtFixedRate(Cache::cleanupExpired, CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
	}

	// ------------------ Public API ------------------

	/**
	 * 获取缓存值（两级 key）
	 */
	public static <T> T get(Object key, Object subKey) {
		CacheData data = getData(key, subKey);
		return data != null ? (T) data.getValue() : null;
	}

	/**
	 * 获取缓存值，若不存在则返回默认值
	 */
	public static <T> T getOrDefault(Object key, Object subKey, T defaultValue) {
		T val = get(key, subKey);
		return val != null ? val : defaultValue;
	}

	/**
	 * 获取缓存值（单级 key，使用 ROOT_KEY）
	 */
	public static <T> T get(Object key) {
		return get(ROOT_KEY, key);
	}

	public static <T> T getOrDefault(Object key, T defaultValue) {
		return getOrDefault(ROOT_KEY, key, defaultValue);
	}

	/**
	 * 放入缓存，使用默认 TTL（1 小时）
	 */
	public static <T> CacheData put(Object key, Object subKey, T value) {
		return put(key, subKey, value, DEFAULT_TTL_MS);
	}

	/**
	 * 放入缓存，指定 TTL（毫秒），若 ttl <= 0 表示永不过期
	 */
	public static <T> CacheData put(Object key, Object subKey, T value, long ttlMs) {
		Map<Object, CacheData> subMap = CACHE.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
		long expiry = (ttlMs <= 0) ? Long.MAX_VALUE : System.currentTimeMillis() + ttlMs;
		return subMap.put(subKey, new CacheData(value, expiry));
	}

	/**
	 * 单级 key put（ROOT_KEY）
	 */
	public static <T> CacheData put(Object key, T value) {
		return put(ROOT_KEY, key, value);
	}

	public static <T> CacheData put(Object key, T value, long ttlMs) {
		return put(ROOT_KEY, key, value, ttlMs);
	}

	/**
	 * 支持懒加载：若 key/subKey 不存在，则调用 supplier 计算并缓存
	 */
	public static <T> T getOrCompute(Object key, Object subKey, Supplier<T> supplier, long ttlMs) {
		return computeIfAbsent(key, subKey, supplier, ttlMs);
	}

	public static <T> T getOrCompute(Object key, Supplier<T> supplier) {
		return getOrCompute(ROOT_KEY, key, supplier, DEFAULT_TTL_MS);
	}

	// ------------------ URI Helper (optional) ------------------

	/**
	 * 从 URI 格式提取缓存（如 "user/123" → key="user", subKey="123"）
	 */
	public static <T> T getFromUri(String uri) {
		String[] parts = splitUri(uri);
		if (parts.length < 2) return null;
		return get(parts[0], parts[1]);
	}

	public static <T> void putToUri(String uri, T value, long ttlMs) {
		String[] parts = splitUri(uri);
		if (parts.length >= 2) {
			put(parts[0], parts[1], value, ttlMs);
		}
	}

	public static <T> void putToUri(String uri, T value) {
		putToUri(uri, value, DEFAULT_TTL_MS);
	}

	private static String[] splitUri(String uri) {
		return uri.split("[/.\\\\]+");
	}

	// ------------------ Internal ------------------

	private static CacheData getData(Object key, Object subKey) {
		Map<Object, CacheData> subMap = CACHE.get(key);
		if (subMap == null) return null;
		CacheData data = subMap.get(subKey);
		if (data == null || data.isExpired()) {
			// Lazy cleanup on access
			if (data != null) subMap.remove(subKey);
			return null;
		}
		return data;
	}

	private static <T> T computeIfAbsent(Object key, Object subKey, Supplier<T> supplier, long ttlMs) {
		Map<Object, CacheData> subMap = CACHE.computeIfAbsent(key, k -> new ConcurrentHashMap<>());

		// 使用 compute 保证原子性：整个「读-判-写」过程对 subKey 是线程安全的
		CacheData resultData = subMap.compute(subKey, (k, existingData) -> {
			long now = System.currentTimeMillis();

			// Step 1: 检查是否存在 & 是否未过期
			if (existingData != null && !existingData.isExpiredAt(now)) {
				// 命中有效缓存：直接复用（保持原 expiryTime）
				return existingData;
			}

			// Step 2: 缓存未命中 或 已过期 → 需要重新计算
			try {
				T newValue = supplier.get();  // 懒加载计算
				if (newValue == null) {
					// 若 supplier 返回 null，按策略：不缓存 null（避免污染），返回 null CacheData
					// 或可根据需求改为缓存 null（需业务确认）
					return null; // ConcurrentHashMap 会 remove(subKey)
				}

				// Step 3: 计算新过期时间
				long expiry;
				if (ttlMs <= 0) {
					expiry = Long.MAX_VALUE; // 永不过期
				} else {
					expiry = now + ttlMs;
				}

				// Step 4: 构建新 CacheData
				return new CacheData(newValue, expiry);

			} catch (Exception e) {
				// 关键：若 supplier 抛异常，不应存入缓存，避免污染
				// 可选：记录日志、指标上报
				// 此处静默失败，返回 null → 等效于不缓存（下次仍可重试）
				// 也可选择 rethrow，取决于业务容忍度
				return null;
			}
		});

		// Step 5: 返回最终值（注意：compute 可能返回 null，如 supplier 返回 null 或异常）
		return resultData != null ? (T) resultData.getValue() : null;
	}

	/**
	 * 清理所有过期项（后台定时 + 懒清理）
	 */
	public static void cleanupExpired() {
		long now = System.currentTimeMillis();
		CACHE.forEach((key, subMap) -> {
			subMap.entrySet().removeIf(entry -> {
				CacheData data = entry.getValue();
				return data.isExpiredAt(now);
			});
			// Optional: remove empty top-level maps
			if (subMap.isEmpty()) {
				CACHE.remove(key);
			}
		});
	}

	// ------------------ CacheData ------------------

	public static class CacheData {
		private final Object value;
		private long expiryTime; // timestamp of expiry, Long.MAX_VALUE means infinite

		CacheData(Object value, long expiryTime) {
			this.value = value;
			this.expiryTime = expiryTime;
		}

		public Object getValue() {
			return value;
		}

		public long getExpiryTime() {
			return expiryTime;
		}

		public boolean isExpired() {
			return isExpiredAt(System.currentTimeMillis());
		}

		public boolean isExpiredAt(long now) {
			return expiryTime != Long.MAX_VALUE && now > expiryTime;
		}

		public void setExpiry(int minute) {
			this.expiryTime = System.currentTimeMillis() + minute * 60 * 1000L;
		}
	}

	// ------------------ Utilities ------------------

	/**
	 * 清空全部缓存（调试用）
	 */
	public static void clearAll() {
		CACHE.clear();
	}

	public static int size() {
		return CACHE.values().stream().mapToInt(Map::size).sum();
	}
}