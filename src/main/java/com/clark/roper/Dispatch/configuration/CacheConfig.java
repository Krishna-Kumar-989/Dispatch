package com.clark.roper.Dispatch.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

//Enables Spring Cache abstraction and uses  @Cacheable, @CacheEvict, @CachePut on service methods.

@Configuration
@EnableCaching
public class CacheConfig {
}
