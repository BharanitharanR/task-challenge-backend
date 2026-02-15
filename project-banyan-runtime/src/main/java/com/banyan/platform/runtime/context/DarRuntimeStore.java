package com.banyan.platform.runtime.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

public final class DarRuntimeStore {

    private final Cache<DarId, DarRuntimeContext> cache;

    public DarRuntimeStore(long maxSize, Duration expiry) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expiry)
                .build();
    }

    public DarId register(DarId id, DarRuntimeContext context) {
        cache.put(id, context);
        return id;
    }

    public DarRuntimeContext get(DarId id) {
        DarRuntimeContext ctx = cache.getIfPresent(id);
        if (ctx == null) {
            throw new IllegalStateException("DAR not loaded: " + id);
        }
        return ctx;
    }

    public void unload(DarId id) {
        cache.invalidate(id);
    }
}
