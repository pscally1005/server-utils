package com.scally.serverutils.distribution;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class DistTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("list", "get", "save", "delete", "update");

    private final SavedDistributionStore store;

    public DistTabCompleter(@NotNull SavedDistributionStore store) {
        this.store = store;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                  @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }
        final String sub = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2) {
            return switch (sub) {
                case "list" -> filterPrefix(onlineNames(), args[1]);
                case "get" -> filterPrefix(merge(onlineNames(), store.getKeysSorted(player.getUniqueId())), args[1]);
                case "save", "update", "delete" ->
                        filterPrefix(store.getKeysSorted(player.getUniqueId()), args[1]);
                default -> List.of();
            };
        }
        if (args.length == 3 && sub.equals("get")) {
            final OfflinePlayerLookup lookup = OfflinePlayerLookup.tryResolve(args[1]);
            if (lookup == null) {
                return List.of();
            }
            return filterPrefix(store.getKeysSorted(lookup.uuid()), args[2]);
        }
        return List.of();
    }

    private @NotNull List<String> onlineNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).sorted().toList();
    }

    private static @NotNull List<String> merge(@NotNull List<String> first, @NotNull List<String> second) {
        return Stream.concat(first.stream(), second.stream()).distinct().sorted().toList();
    }

    private static @NotNull List<String> filterPrefix(@NotNull List<String> options, @NotNull String prefix) {
        final String p = prefix.toLowerCase(Locale.ROOT);
        final List<String> out = new ArrayList<>();
        for (String o : options) {
            if (o.toLowerCase(Locale.ROOT).startsWith(p)) {
                out.add(o);
            }
        }
        return out;
    }

    private record OfflinePlayerLookup(@NotNull java.util.UUID uuid) {
        static @Nullable OfflinePlayerLookup tryResolve(@NotNull String name) {
            final var offline = Bukkit.getOfflinePlayer(name);
            if (!offline.hasPlayedBefore() && !offline.isOnline()) {
                return null;
            }
            return new OfflinePlayerLookup(offline.getUniqueId());
        }
    }
}
