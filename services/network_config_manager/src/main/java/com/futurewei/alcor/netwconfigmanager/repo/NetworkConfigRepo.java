package com.futurewei.alcor.netwconfigmanager.repo;

import com.futurewei.alcor.common.db.CacheException;
import com.futurewei.alcor.common.db.CacheFactory;
import com.futurewei.alcor.common.db.ICache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class NetworkConfigRepo {

    private static final String NCM_CACHE_NAME = "NCMCache";
    private ICache<String, List> cache;
    private CacheFactory cacheFactory;


    private String getNcmCacheName(String suffix) {
        return NCM_CACHE_NAME + suffix;
    }

    @Autowired
    public NetworkConfigRepo(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
        cache = cacheFactory.getCache(List.class, NCM_CACHE_NAME);
    }

    public void addListResourceIdItem(String hostId, List resourceIdList) throws CacheException {
        ICache<String, List> listICache = cacheFactory.getCache(List.class, getNcmCacheName("resourceIdList"));
        listICache.put(hostId, resourceIdList);
    }

    public void addResourceStateItem(String resourceId, Object resourceState) throws CacheException {
        ICache<String, Object> resourceStateICache = cacheFactory.getCache(Object.class, getNcmCacheName("resourceState"));
        resourceStateICache.put(resourceId, resourceState);
    }
}
