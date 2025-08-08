package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.client.LegacySystemClient;
import com.cleverdev.migrator.dto.LegacyClientRecord;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LegacySystemAgencyCacheTest {
    @DisplayName("getAgencyNameByClientGUID should return agency name for valid GUID")
    @Test
    void getAgencyNameByClientGUIDReturnsAgencyNameForValidGUID() {
        LegacySystemClient legacySystemClient = mock(LegacySystemClient.class);
        when(legacySystemClient.getClients()).thenReturn(List.of(
                new LegacyClientRecord("Agency1", "guid1", null, null, null, null, null),
                new LegacyClientRecord("Agency2", "guid2", null, null, null, null, null)
        ));
        LegacySystemAgencyCache cache = new LegacySystemAgencyCache(legacySystemClient);

        Optional<String> result = cache.getAgencyNameByClientGUID("guid1");

        assertTrue(result.isPresent());
        assertEquals("Agency1", result.get());
    }

    @DisplayName("getAgencyNameByClientGUID should return empty optional for invalid GUID")
    @Test
    void getAgencyNameByClientGUIDReturnsEmptyOptionalForInvalidGUID() {
        LegacySystemClient legacySystemClient = mock(LegacySystemClient.class);
        when(legacySystemClient.getClients()).thenReturn(List.of(
                new LegacyClientRecord("Agency1", "guid1", null, null, null, null, null),
                new LegacyClientRecord("Agency2", "guid2", null, null, null, null, null)
        ));
        LegacySystemAgencyCache cache = new LegacySystemAgencyCache(legacySystemClient);

        Optional<String> result = cache.getAgencyNameByClientGUID("invalid-guid");

        assertTrue(result.isEmpty());
    }

    @DisplayName("getAgencyNameByClientGUID should handle null GUID gracefully")
    @Test
    void getAgencyNameByClientGUIDHandlesNullGUIDGracefully() {
        LegacySystemClient legacySystemClient = mock(LegacySystemClient.class);
        when(legacySystemClient.getClients()).thenReturn(List.of(
                new LegacyClientRecord("Agency1", "guid1", null, null, null, null, null),
                new LegacyClientRecord("Agency2", "guid2", null, null, null, null, null)
        ));
        LegacySystemAgencyCache cache = new LegacySystemAgencyCache(legacySystemClient);

        Optional<String> result = cache.getAgencyNameByClientGUID(null);

        assertTrue(result.isEmpty());
    }

    @DisplayName("getAgencyNameByClientGUID should handle empty cache")
    @Test
    void getAgencyNameByClientGUIDHandlesEmptyCache() {
        LegacySystemClient legacySystemClient = mock(LegacySystemClient.class);
        when(legacySystemClient.getClients()).thenReturn(List.of());
        LegacySystemAgencyCache cache = new LegacySystemAgencyCache(legacySystemClient);

        Optional<String> result = cache.getAgencyNameByClientGUID("guid1");

        assertTrue(result.isEmpty());
    }
}
