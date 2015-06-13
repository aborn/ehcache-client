package org.popkit.mobile.entity;

import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * use record register cache key info
 * @author Aborn Jiang
 * @email aborn.jiang # foxmail
 * @date 06-13-2015
 * @time 11:11 PM
 */
public class MCacheInfo {
    private static final long DEFAULT_TIME_TO_IDLE = 5*60;   // 5分钟
    private static final long DEFAULT_TIME_TO_LIVE = 10*60;  // 10分钟

    /**
     * cache name, cannot use "default"
     */
    private String key;

    /**
     * max entries in memory, 0 no limit
     */
    private int maxEntriesLocalHeap;

    /**
     * default false
     */
    private boolean eternal;

    /**
     * default DEFAULT_TIME_TO_IDLE
     */
    private long timeToIdleSeconds;

    /**
     * DEFAULT_TIME_TO_LIVE
     */
    private long timeToLiveSeconds;

    /**
     * default LFU, can also be LRU, FIFO
     */
    private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy;

    public MCacheInfo(String cacheKey) {
        this(cacheKey, 0);
    }

    public MCacheInfo(String cacheKey, int maxEntriesLocalHeap) {
        this(cacheKey, maxEntriesLocalHeap, DEFAULT_TIME_TO_LIVE);
    }

    public MCacheInfo(String cacheKey, int maxEntriesLocalHeap, long timeToLiveSeconds) {
        this(cacheKey, maxEntriesLocalHeap, false,
                timeToLiveSeconds > DEFAULT_TIME_TO_IDLE ? DEFAULT_TIME_TO_LIVE : timeToLiveSeconds,
                timeToLiveSeconds, MemoryStoreEvictionPolicy.LFU);
    }

    public MCacheInfo(String key, int maxEntriesLocalHeap, boolean eternal, long timeToIdleSeconds,
                      long timeToLiveSeconds, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
        this.key = key;
        this.eternal = eternal;
        this.timeToIdleSeconds = timeToIdleSeconds;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    @Override
    public String toString() {
        return "[key:" + key + ", maxEntriesLocalHeap:" + maxEntriesLocalHeap +
                ", eternal:" + eternal + ", timeToIdleSeconds:" + timeToIdleSeconds +
                ", timeToLiveSeconds:" + timeToLiveSeconds + ", memoryStoreEvictionPolicy:" + memoryStoreEvictionPolicy +
                "]";
    }

    public String getKey() {
        return key;
    }

    public boolean isEternal() {
        return eternal;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public int getMaxEntriesLocalHeap() {
        return maxEntriesLocalHeap;
    }

    public void setMaxEntriesLocalHeap(int maxEntriesLocalHeap) {
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
