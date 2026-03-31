package com.scally.serverutils.distribution;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Persists per-player named distribution strings under the plugin data folder.
 */
public final class SavedDistributionStore {

    public static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,32}$");
    public static final int MAX_VALUE_LENGTH = 1024;
    public static final int MAX_KEYS_PER_PLAYER = 128;
    private static final String FILE_NAME = "distributions.yml";
    private static final String PLAYERS_PATH = "players";

    private final File yamlFile;
    private final Map<UUID, Map<String, String>> byPlayer = new HashMap<>();

    public SavedDistributionStore(@NotNull File dataFolder) {
        this.yamlFile = new File(dataFolder, FILE_NAME);
    }

    public static @Nullable String validateKey(@Nullable String key) {
        if (key == null || key.isEmpty()) {
            return "Key must be non-empty.";
        }
        if (!KEY_PATTERN.matcher(key).matches()) {
            return "Key must be 1–32 characters: letters, digits, underscore, or hyphen.";
        }
        return null;
    }

    public static @Nullable String validateValueLength(@Nullable String value) {
        if (value == null) {
            return "Distribution string is missing.";
        }
        if (value.length() > MAX_VALUE_LENGTH) {
            return "Distribution string is too long (max " + MAX_VALUE_LENGTH + " characters).";
        }
        return null;
    }

    public void load() {
        byPlayer.clear();
        if (!yamlFile.exists()) {
            return;
        }
        final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
        final ConfigurationSection players = yaml.getConfigurationSection(PLAYERS_PATH);
        if (players == null) {
            return;
        }
        for (String uuidStr : players.getKeys(false)) {
            final UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            final ConfigurationSection keysSection = players.getConfigurationSection(uuidStr);
            if (keysSection == null) {
                continue;
            }
            final Map<String, String> forPlayer = new HashMap<>();
            for (String key : keysSection.getKeys(false)) {
                final String raw = keysSection.getString(key);
                if (raw != null && validateKey(key) == null) {
                    forPlayer.put(key, raw);
                }
            }
            if (!forPlayer.isEmpty()) {
                byPlayer.put(uuid, forPlayer);
            }
        }
    }

    public void save() throws IOException {
        final YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, String>> e : byPlayer.entrySet()) {
            final String base = PLAYERS_PATH + "." + e.getKey();
            for (Map.Entry<String, String> kv : e.getValue().entrySet()) {
                yaml.set(base + "." + kv.getKey(), kv.getValue());
            }
        }
        final File parent = yamlFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create data folder: " + parent);
        }
        yaml.save(yamlFile);
    }

    public @NotNull List<String> getKeysSorted(@NotNull UUID owner) {
        final Map<String, String> map = byPlayer.get(owner);
        if (map == null || map.isEmpty()) {
            return List.of();
        }
        final List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        return keys;
    }

    public @Nullable String get(@NotNull UUID owner, @NotNull String key) {
        final Map<String, String> map = byPlayer.get(owner);
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public boolean contains(@NotNull UUID owner, @NotNull String key) {
        return get(owner, key) != null;
    }

    /**
     * @return {@link PutIfAbsentResult#SUCCESS} if stored; otherwise reason for failure (caller should validate key/value first)
     */
    public @NotNull PutIfAbsentResult putIfAbsent(@NotNull UUID owner, @NotNull String key, @NotNull String value) {
        final Map<String, String> map = byPlayer.computeIfAbsent(owner, u -> new HashMap<>());
        if (map.containsKey(key)) {
            return PutIfAbsentResult.DUPLICATE;
        }
        if (map.size() >= MAX_KEYS_PER_PLAYER) {
            return PutIfAbsentResult.MAX_KEYS;
        }
        map.put(key, value);
        return PutIfAbsentResult.SUCCESS;
    }

    /**
     * @return {@link ReplaceResult#SUCCESS} if updated; {@link ReplaceResult#NOT_FOUND} if no such key
     */
    public @NotNull ReplaceResult replace(@NotNull UUID owner, @NotNull String key, @NotNull String value) {
        final Map<String, String> map = byPlayer.get(owner);
        if (map == null || !map.containsKey(key)) {
            return ReplaceResult.NOT_FOUND;
        }
        map.put(key, value);
        return ReplaceResult.SUCCESS;
    }

    /**
     * @return true if a key was removed
     */
    public boolean remove(@NotNull UUID owner, @NotNull String key) {
        final Map<String, String> map = byPlayer.get(owner);
        if (map == null) {
            return false;
        }
        final String removed = map.remove(key);
        if (map.isEmpty()) {
            byPlayer.remove(owner);
        }
        return removed != null;
    }

    public enum PutIfAbsentResult {
        SUCCESS,
        DUPLICATE,
        MAX_KEYS
    }

    public enum ReplaceResult {
        SUCCESS,
        NOT_FOUND
    }
}
