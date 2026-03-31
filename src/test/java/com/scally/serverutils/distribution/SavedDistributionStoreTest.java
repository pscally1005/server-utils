package com.scally.serverutils.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SavedDistributionStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void validateKey_rejectsNullEmptyAndInvalid() {
        assertEquals("Key must be non-empty.", SavedDistributionStore.validateKey(null));
        assertEquals("Key must be non-empty.", SavedDistributionStore.validateKey(""));
        assertEquals(
                "Key must be 1–32 characters: letters, digits, underscore, or hyphen.",
                SavedDistributionStore.validateKey("bad key"));
        assertEquals(
                "Key must be 1–32 characters: letters, digits, underscore, or hyphen.",
                SavedDistributionStore.validateKey("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertNull(SavedDistributionStore.validateKey("town_path"));
        assertNull(SavedDistributionStore.validateKey("a"));
    }

    @Test
    void validateValueLength_rejectsNullTooLong() {
        assertEquals("Distribution string is missing.", SavedDistributionStore.validateValueLength(null));
        assertEquals(
                "Distribution string is too long (max " + SavedDistributionStore.MAX_VALUE_LENGTH + " characters).",
                SavedDistributionStore.validateValueLength("x".repeat(SavedDistributionStore.MAX_VALUE_LENGTH + 1)));
        assertNull(SavedDistributionStore.validateValueLength("stone"));
    }

    @Test
    void saveAndLoad_roundTrip() throws IOException {
        final UUID owner = UUID.randomUUID();
        final SavedDistributionStore store = new SavedDistributionStore(tempDir.toFile());
        assertEquals(SavedDistributionStore.PutIfAbsentResult.SUCCESS, store.putIfAbsent(owner, "path", "stone"));
        store.save();

        final SavedDistributionStore loaded = new SavedDistributionStore(tempDir.toFile());
        loaded.load();
        assertEquals("stone", loaded.get(owner, "path"));
        assertEquals("path", loaded.getKeysSorted(owner).getFirst());
    }

    @Test
    void putIfAbsent_duplicateAndMaxKeys() {
        final UUID owner = UUID.randomUUID();
        final SavedDistributionStore store = new SavedDistributionStore(tempDir.toFile());
        assertEquals(SavedDistributionStore.PutIfAbsentResult.SUCCESS, store.putIfAbsent(owner, "a", "stone"));
        assertEquals(SavedDistributionStore.PutIfAbsentResult.DUPLICATE, store.putIfAbsent(owner, "a", "dirt"));
        assertEquals("stone", store.get(owner, "a"));

        final UUID other = UUID.randomUUID();
        for (int i = 0; i < SavedDistributionStore.MAX_KEYS_PER_PLAYER; i++) {
            assertEquals(
                    SavedDistributionStore.PutIfAbsentResult.SUCCESS,
                    store.putIfAbsent(other, "k" + i, "stone"),
                    "i=" + i);
        }
        assertEquals(SavedDistributionStore.PutIfAbsentResult.MAX_KEYS, store.putIfAbsent(other, "extra", "stone"));
    }

    @Test
    void replace_notFoundThenSuccess() {
        final UUID owner = UUID.randomUUID();
        final SavedDistributionStore store = new SavedDistributionStore(tempDir.toFile());
        assertEquals(SavedDistributionStore.ReplaceResult.NOT_FOUND, store.replace(owner, "nope", "dirt"));
        assertEquals(SavedDistributionStore.PutIfAbsentResult.SUCCESS, store.putIfAbsent(owner, "a", "stone"));
        assertEquals(SavedDistributionStore.ReplaceResult.SUCCESS, store.replace(owner, "a", "dirt"));
        assertEquals("dirt", store.get(owner, "a"));
    }

    @Test
    void remove_lastKeyDropsPlayerEntry() {
        final UUID owner = UUID.randomUUID();
        final SavedDistributionStore store = new SavedDistributionStore(tempDir.toFile());
        store.putIfAbsent(owner, "only", "stone");
        assertTrue(store.remove(owner, "only"));
        assertNull(store.get(owner, "only"));
        assertTrue(store.getKeysSorted(owner).isEmpty());
        assertFalse(store.remove(owner, "only"));
    }
}
