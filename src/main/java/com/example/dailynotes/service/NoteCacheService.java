package com.example.dailynotes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Утилитный сервис для взаимодействия с кэшом заметок в Redis.
 */
@Service
public class NoteCacheService {

    public static final String NOTES_BY_DATE_CACHE = "notesByDate";
    public static final String NOTES_BY_ID_CACHE = "notesById";

    private static final Logger logger = LoggerFactory.getLogger(NoteCacheService.class);

    private final CacheManager cacheManager;

    public NoteCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictNoteById(Long noteId) {
        evict(NOTES_BY_ID_CACHE, noteId);
    }

    public void evictNotesByDate(LocalDate date) {
        if (date == null) {
            clearCache(NOTES_BY_DATE_CACHE);
        } else {
            evict(NOTES_BY_DATE_CACHE, date);
        }
    }

    public void evictNotesByDates(LocalDate... dates) {
        if (dates == null) {
            return;
        }
        Arrays.stream(dates).filter(Objects::nonNull).forEach(this::evictNotesByDate);
    }

    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            logger.debug("Очищен кэш {}", cacheName);
        }
    }

    private void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null && key != null) {
            cache.evict(key);
            logger.debug("Удален ключ {} из кэша {}", key, cacheName);
        }
    }
}


