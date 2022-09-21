package com.scally.serverutils.slabs;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.distribution.DistributionPair;
import com.scally.serverutils.distribution.DistributionTabCompleter;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO: unit tests

public class SlabsCommandExecutor implements CommandExecutor, DistributionTabCompleter {

    private final UndoManager undoManager;

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .build();

    public SlabsCommandExecutor(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    // /slabs <x1> <y1> <z1> <x2> <y2> <z2> <from-slab> <to-slab>
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        final ValidationResult validationResult = inputValidator.validate(commandSender, args);
        if (!validationResult.validated()) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Coordinates coordinates = validationResult.coordinates();

        Distribution fromDistribution = isValidSlabsDistribution(args[6]);
        Distribution toDistribution = isValidSlabsDistribution(args[7]);
        if(fromDistribution == null || toDistribution == null) {
            ChatMessageUtils.sendError(commandSender, "Slab blocks must be valid!");
            return false;
        }

        World world = player.getWorld();

        final SlabsChangeset changeset = new SlabsChangeset();

        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);
                    BlockData bd = block.getBlockData();
                    Material mat = bd.getMaterial();

                    if(fromDistribution.hasMaterial(mat)) {

                        Slab slab = (Slab) bd;
                        Slab.Type type = slab.getType();
                        boolean isWaterlogged = slab.isWaterlogged();

                        Material toMaterial = toDistribution.pick();
                        block.setType(toMaterial, false);
                        bd = block.getBlockData();
                        ((Slab) bd).setWaterlogged(isWaterlogged);
                        ((Slab) bd).setType(type);
                        world.setBlockData(x, y, z, bd);

                        final Location loc = block.getLocation();
                        SlabsChange slabsChange = new SlabsChange(loc, slab.getMaterial(), toMaterial, type, isWaterlogged);
                        changeset.add(slabsChange);

                    }

                }
            }
        }

        undoManager.store(player, changeset);
        ChatMessageUtils.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changeset.count()));
        return true;
    }

    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.SLABS);
    }

    private Distribution isValidSlabsDistribution(String arg) {

        final Distribution dist = Distribution.parse(arg);
        if(dist == null) {
            return null;
        }

        final List<DistributionPair> fromPairs = dist.getPairs();
        for(DistributionPair pair : fromPairs) {
            final BlockData blockData = pair.getMaterial().createBlockData();
            if(!(blockData instanceof Slab)) {
                return null;
            }
        }

        return dist;

    }

}
