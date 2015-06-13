package org.popkit.mobile.core;

import org.popkit.mobile.entity.MCacheInfo;
import org.popkit.mobile.entity.MCacheKey;

import java.util.List;

/**
 * memory cache serivce
 * @author Aborn Jiang
 * @email aborn.jiang AT foxmail.com
 * @date 06-13-2015
 * @time 10:39 PM
 */
public interface MCacheService {

    /**
     * @param key
     * @param object
     */
    public boolean add(MCacheKey key, Object object);

    /**
     * @param keys
     * @param objs
     * @param <T>
     * @return
     */
    public <T> boolean mAdd(List<MCacheKey> keys, List<T> objs);

    /**
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(MCacheKey key);

    /**
     * @param keys
     * @param <T>
     * @return
     */
    public <T> List<T> mGet(List<MCacheKey> keys);

    /**
     * @param key
     */
    public void remove(MCacheKey key);

    /**
     * @param cacheKey
     * @return
     */
    public boolean cacheExists(String cacheKey);

    /**
     * register a local memory cache
     */
    public boolean register(MCacheInfo cacheInfo);

    /**
     * @param cacheKey
     */
    public boolean makeEternal(String cacheKey);


    /**
     * @param cacheKey
     */
    public boolean existEternal(String cacheKey);

    /**
     * update cache config
     * @param cacheKey
     * @param maxEntriesLocalHeap
     * @param timeToLiveSeconds
     */
    public void update(String cacheKey, int maxEntriesLocalHeap, int timeToLiveSeconds);
}
