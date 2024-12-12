package com.example.cuentaservice.cache;

import com.example.cuentaservice.dto.cache.ClienteCacheDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClienteCache {

    private final Cache<Long, ClienteCacheDTO> cache;

    public ClienteCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    public void putCliente(Long clienteId, ClienteCacheDTO cliente) {
        cache.put(clienteId, cliente);
    }

    public ClienteCacheDTO getCliente(Long clienteId) {
        return cache.getIfPresent(clienteId);
    }

    public void removeCliente(Long clienteId) {
        cache.invalidate(clienteId);
    }
}
