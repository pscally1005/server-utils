package com.scally.serverutils.distribution;

import com.scally.serverutils.chat.ChatMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DistCommandExecutor implements CommandExecutor {

    private static final String PERM_READ = "scally.dist.read";
    private static final String PERM_WRITE = "scally.dist.write";

    private final SavedDistributionStore store;

    public DistCommandExecutor(@NotNull SavedDistributionStore store) {
        this.store = store;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ChatMessageUtils.sendError(sender, "This command can only be used by players.");
            return true;
        }
        if (!player.hasPermission(PERM_READ)) {
            ChatMessageUtils.sendError(sender, "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        final String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "list" -> handleList(player, args);
            case "get" -> handleGet(player, args);
            case "save" -> handleSave(player, args);
            case "update" -> handleUpdate(player, args);
            case "delete" -> handleDelete(player, args);
            default -> false;
        };
    }

    private boolean handleList(@NotNull Player player, @NotNull String[] args) {
        if (args.length > 2) {
            return false;
        }
        if (args.length == 1) {
            final var keys = store.getKeysSorted(player.getUniqueId());
            if (keys.isEmpty()) {
                ChatMessageUtils.sendSuccess(player, "No saved distributions.");
            } else {
                ChatMessageUtils.sendSuccess(player, String.join(", ", keys));
            }
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            ChatMessageUtils.sendError(player, "Unknown or never-seen player: " + args[1]);
            return true;
        }
        final var keys = store.getKeysSorted(target.getUniqueId());
        if (keys.isEmpty()) {
            ChatMessageUtils.sendSuccess(player, "No saved distributions for " + args[1] + ".");
        } else {
            ChatMessageUtils.sendSuccess(player, String.join(", ", keys));
        }
        return true;
    }

    private boolean handleGet(@NotNull Player player, @NotNull String[] args) {
        if (args.length == 2) {
            final String key = args[1];
            final String err = SavedDistributionStore.validateKey(key);
            if (err != null) {
                ChatMessageUtils.sendError(player, err);
                return true;
            }
            final String value = store.get(player.getUniqueId(), key);
            if (value == null) {
                ChatMessageUtils.sendError(player, "No distribution named \"" + key + "\".");
                return true;
            }
            ChatMessageUtils.sendSuccess(player, value);
            return true;
        }
        if (args.length == 3) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                ChatMessageUtils.sendError(player, "Unknown or never-seen player: " + args[1]);
                return true;
            }
            final String key = args[2];
            final String err = SavedDistributionStore.validateKey(key);
            if (err != null) {
                ChatMessageUtils.sendError(player, err);
                return true;
            }
            final String value = store.get(target.getUniqueId(), key);
            if (value == null) {
                ChatMessageUtils.sendError(player, "No distribution \"" + key + "\" for " + args[1] + ".");
                return true;
            }
            ChatMessageUtils.sendSuccess(player, value);
            return true;
        }
        return false;
    }

    private boolean handleSave(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(PERM_WRITE)) {
            ChatMessageUtils.sendError(player, "You do not have permission to save distributions.");
            return true;
        }
        if (args.length < 3) {
            return false;
        }
        final String key = args[1];
        final String keyErr = SavedDistributionStore.validateKey(key);
        if (keyErr != null) {
            ChatMessageUtils.sendError(player, keyErr);
            return true;
        }
        final String joined = joinDistributionArgs(args, 2);
        final String lenErr = SavedDistributionStore.validateValueLength(joined);
        if (lenErr != null) {
            ChatMessageUtils.sendError(player, lenErr);
            return true;
        }
        try {
            DistributionParser.parse(joined);
        } catch (InvalidDistributionException exception) {
            ChatMessageUtils.sendError(player, exception.getMessage());
            return true;
        }
        final SavedDistributionStore.PutIfAbsentResult result =
                store.putIfAbsent(player.getUniqueId(), key, joined);
        return switch (result) {
            case SUCCESS -> {
                try {
                    store.save();
                } catch (IOException e) {
                    store.remove(player.getUniqueId(), key);
                    ChatMessageUtils.sendError(player, "Failed to save file: " + e.getMessage());
                    yield true;
                }
                ChatMessageUtils.sendSuccess(player, "Saved distribution \"" + key + "\".");
                yield true;
            }
            case DUPLICATE -> {
                ChatMessageUtils.sendError(player, "A distribution named \"" + key + "\" already exists. Use update.");
                yield true;
            }
            case MAX_KEYS -> {
                ChatMessageUtils.sendError(player, "You have reached the maximum number of saved distributions ("
                        + SavedDistributionStore.MAX_KEYS_PER_PLAYER + ").");
                yield true;
            }
        };
    }

    private boolean handleUpdate(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(PERM_WRITE)) {
            ChatMessageUtils.sendError(player, "You do not have permission to update distributions.");
            return true;
        }
        if (args.length < 3) {
            return false;
        }
        final String key = args[1];
        final String keyErr = SavedDistributionStore.validateKey(key);
        if (keyErr != null) {
            ChatMessageUtils.sendError(player, keyErr);
            return true;
        }
        final String joined = joinDistributionArgs(args, 2);
        final String lenErr = SavedDistributionStore.validateValueLength(joined);
        if (lenErr != null) {
            ChatMessageUtils.sendError(player, lenErr);
            return true;
        }
        try {
            DistributionParser.parse(joined);
        } catch (InvalidDistributionException exception) {
            ChatMessageUtils.sendError(player, exception.getMessage());
            return true;
        }
        final SavedDistributionStore.ReplaceResult result = store.replace(player.getUniqueId(), key, joined);
        if (result == SavedDistributionStore.ReplaceResult.NOT_FOUND) {
            ChatMessageUtils.sendError(player, "No distribution named \"" + key + "\" to update.");
            return true;
        }
        try {
            store.save();
        } catch (IOException e) {
            ChatMessageUtils.sendError(player, "Failed to save file: " + e.getMessage());
            return true;
        }
        ChatMessageUtils.sendSuccess(player, "Updated distribution \"" + key + "\".");
        return true;
    }

    private boolean handleDelete(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(PERM_WRITE)) {
            ChatMessageUtils.sendError(player, "You do not have permission to delete distributions.");
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        final String key = args[1];
        final String keyErr = SavedDistributionStore.validateKey(key);
        if (keyErr != null) {
            ChatMessageUtils.sendError(player, keyErr);
            return true;
        }
        if (!store.remove(player.getUniqueId(), key)) {
            ChatMessageUtils.sendError(player, "No distribution named \"" + key + "\" to delete.");
            return true;
        }
        try {
            store.save();
        } catch (IOException e) {
            ChatMessageUtils.sendError(player, "Failed to save file: " + e.getMessage());
            return true;
        }
        ChatMessageUtils.sendSuccess(player, "Deleted distribution \"" + key + "\".");
        return true;
    }

    private static @NotNull String joinDistributionArgs(@NotNull String[] args, int fromInclusive) {
        return Stream.of(args).skip(fromInclusive).collect(Collectors.joining(" "));
    }
}
