package org.popkit.mobile.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.popkit.mobile.core.MCacheService;
import org.popkit.mobile.entity.MCacheInfo;
import org.popkit.mobile.entity.MCacheKey;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * use ehcache as MCacheService implementation.
 * @author Aborn Jiang
 * @email aborn.jiang # foxmail.com
 * @date 06-13-2015
 * @time 10:40 PM
 */
@Service
public class EhCacheService implements MCacheService {
    // create cacheManager use .xml file
    private static volatile CacheManager cacheManager = CacheManager.newInstance(EhCacheService.class.getClassLoader().getResourceAsStream("default-ehcache.xml"));

    // record all register cache info
    private static final ConcurrentHashMap<String, MCacheInfo> REGISTER_CACHE = new ConcurrentHashMap<String, MCacheInfo>();

    @Override
    public boolean add(MCacheKey key, Object object) {
        try {
            Cache cache = cacheManager.getCache(key.getKey());

            if (null == cache) {
                return false;
            }

            Element element = new Element(key.getSubKey(), object);
            cache.put(element);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public <T> boolean mAdd(List<MCacheKey> keys, List<T> objs) {
        try {
            if (CollectionUtils.isEmpty(keys)
                    || keys.size() != objs.size()) {
                return false;
            }

            Cache cache = cacheManager.getCache(keys.get(0).getKey());
            if (null == cache) {
                return false;
            }

            List<Element> elements = new ArrayList<Element>();

            int i = 0;
            for (MCacheKey key : keys) {
                elements.add(new Element(key.getKey(), objs.get(i++)));
            }

            cache.putAll(elements);
            return false;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public <T> T get(MCacheKey key) {
        try {
            Cache cache = cacheManager.getCache(key.getKey());

            if (null == cache) {
                return null;
            }

            Element element = cache.get(key.getSubKey());
            if (null != element) {   // hit
                return (T) element.getObjectValue();
            } else {                 // missed
                return null;
            }
        } catch (Throwable throwable) {
            return null;
        }
    }

    @Override
    public <T> List<T> mGet(List<MCacheKey> keys) {
        try {
            if (CollectionUtils.isEmpty(keys)
                    || StringUtils.isEmpty(keys.get(0).getKey())) {
                return Collections.EMPTY_LIST;
            }

            Cache cache = cacheManager.getCache(keys.get(0).getKey());

            if (null == cache) {
                return Collections.EMPTY_LIST;
            }
            List<String> subKeys = new ArrayList<String>();

            for (MCacheKey key : keys) {
                subKeys.add(key.getSubKey());
            }
            return (List<T>) cache.getAll(subKeys);
        } catch (Throwable throwable) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void remove(MCacheKey key) {
        Cache cache = cacheManager.getCache(key.getKey());
        if (null != cache) {
            cache.remove(key.getSubKey());
        }
    }

    // remove cache
    public void removeCache(String cacheKey) {
        if (cacheExists(cacheKey)) {
            cacheManager.removeCache(cacheKey);
        }
    }

    @Override
    public boolean cacheExists(String cacheKey) {
        return null != cacheManager.getCache(cacheKey);
    }

    @Override
    public synchronized boolean register(MCacheInfo cacheInfo) {
        try {
            if (cacheExists(cacheInfo.getKey()) ||
                    StringUtils.isBlank(cacheInfo.getKey()) ||
                    "default".equals(cacheInfo.getKey())) {
                return false;
            }

            Cache newMemCache = new Cache(cacheInfo.getKey(),
                    cacheInfo.getMaxEntriesLocalHeap(),         // max entries in memory
                    cacheInfo.getMemoryStoreEvictionPolicy(),   // memoryStoreEvictionPolicy
                    false,                 // whether save disk
                    "/data/appdatas",      // disk store path
                    cacheInfo.isEternal(),
                    cacheInfo.getTimeToLiveSeconds(),
                    cacheInfo.getTimeToIdleSeconds(),
                    false,  // whether save disk when jvm restart
                    60 * 60,  // how often to run the disk store expiry thread.
                    null    // registeredEventListeners
            );

            cacheManager.addCache(newMemCache);
            REGISTER_CACHE.put(cacheInfo.getKey(), cacheInfo);
            return cacheExists(cacheInfo.getKey());
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public synchronized boolean makeEternal(String cacheKey) {
        try {
            if (StringUtils.isNotBlank(cacheKey) && cacheExists(cacheKey)) {
                Cache cache = cacheManager.getCache(cacheKey);
                cache.getCacheConfiguration().setEternal(true);

                // udate register cache info
                if (REGISTER_CACHE.containsKey(cacheKey)) {
                    REGISTER_CACHE.get(cacheKey).setEternal(true);
                }
                return true;
            }
            return false;
        } catch (Throwable t) {
            // do nothing
            return false;
        }
    }

    @Override
    public synchronized boolean existEternal(String cacheKey) {
        try {
            if (StringUtils.isNotBlank(cacheKey) && cacheExists(cacheKey)) {
                Cache cache = cacheManager.getCache(cacheKey);
                cache.getCacheConfiguration().setEternal(false);

                // udate register cache info
                if (REGISTER_CACHE.containsKey(cacheKey)) {
                    REGISTER_CACHE.get(cacheKey).setEternal(false);
                }

                return true;
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public synchronized void update(String cacheKey, int maxEntriesLocalHeap, int timeToLiveSeconds) {
        try {

            if (StringUtils.isNotBlank(cacheKey) && cacheExists(cacheKey)) {
                Cache cache = cacheManager.getCache(cacheKey);
                CacheConfiguration config = cache.getCacheConfiguration();
                long oldEntriesLocalHeap = config.getMaxEntriesLocalHeap();
                long oldTimeToLiveSeconds = config.getTimeToLiveSeconds();
                config.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
                config.setTimeToLiveSeconds(timeToLiveSeconds);

                // update register cache info
                if (REGISTER_CACHE.containsKey(cacheKey)) {
                    MCacheInfo mCacheInfo = REGISTER_CACHE.get(cacheKey);
                    mCacheInfo.setTimeToLiveSeconds(timeToLiveSeconds);
                    mCacheInfo.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
                }

                String logMsg = "CacheKey:" + cacheKey + " [maxEntriesLocalHeap](old:" + oldEntriesLocalHeap +
                        ", new:" + maxEntriesLocalHeap + ")" + "[timeToLiveSeconds](old:" + oldTimeToLiveSeconds +
                        ", new:" + timeToLiveSeconds + ")";
            }
        } catch (Throwable t) {
            String logMsg = cacheKey + " [maxEntriesLocalHeap](new:" + maxEntriesLocalHeap + ")" +
                    "[timeToLiveSeconds](new:" + timeToLiveSeconds + ")";
        }
    }
}
