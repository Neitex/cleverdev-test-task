package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.client.LegacySystemClient;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LegacySystemAgencyCache {
    private final Map<String, String> agencyCache;

    public LegacySystemAgencyCache(LegacySystemClient legacySystemClient) {
        agencyCache = legacySystemClient.getClients()
                .stream()
                .map(r -> Map.entry(r.guid(), r.agency()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<String> getAgencyNameByClientGUID(String clientGUID) {
        return Optional.ofNullable(agencyCache.get(clientGUID));
    }
}
