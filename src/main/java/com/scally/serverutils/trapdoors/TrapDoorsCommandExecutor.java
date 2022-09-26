package com.scally.serverutils.trapdoors;

import com.scally.serverutils.chat.ChatMessageUtils;
import com.scally.serverutils.distribution.Distribution;
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
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrapDoorsCommandExecutor implements CommandExecutor, DistributionTabCompleter {

    private final UndoManager undoManager;

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .withFromDistribution(6, TrapDoor.class)
            .withToDistribution(7, TrapDoor.class)
            .build();

    public TrapDoorsCommandExecutor(UndoManager undoManager) { this.undoManager = undoManager; }

    // /trapdoors <x1> <y1> <z1> <x2> <y2> <z2> <from-trapdoor> <to-trapdoor>
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        final ValidationResult validationResult = inputValidator.validate(commandSender, args);
        if (!validationResult.validated()) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Coordinates coordinates = validationResult.coordinates();
        final Distribution fromDistribution = validationResult.fromDistribution();
        final Distribution toDistribution = validationResult.toDistribution();

        World world = player.getWorld();

        final TrapDoorsChangeset changeset = new TrapDoorsChangeset();

        for (int x = coordinates.minX(); x <= coordinates.maxX(); x++) {
            for (int y = coordinates.minY(); y <= coordinates.maxY(); y++) {
                for (int z = coordinates.minZ(); z <= coordinates.maxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);
                    BlockData bd = block.getBlockData();
                    Material mat = bd.getMaterial();

                    if (fromDistribution.hasMaterial(mat)) {

                        TrapDoor trapdoor = (TrapDoor) bd;
                        Bisected.Half half = trapdoor.getHalf();
                        BlockFace facing = trapdoor.getFacing();
                        boolean open = trapdoor.isOpen();
                        boolean powered = trapdoor.isPowered();
                        boolean isWaterlogged = trapdoor.isWaterlogged();

                        Material toMaterial = toDistribution.pick();
                        block.setType(toMaterial, false);
                        bd = block.getBlockData();
                        ((TrapDoor) bd).setHalf(half);
                        ((TrapDoor) bd).setFacing(facing);
                        ((TrapDoor) bd).setOpen(open);
                        ((TrapDoor) bd).setPowered(powered);
                        ((TrapDoor) bd).setWaterlogged(isWaterlogged);
                        world.setBlockData(x, y, z, bd);

                        final Location loc = block.getLocation();
                        TrapDoorsChange trapdoorsChange = new TrapDoorsChange(loc, trapdoor.getMaterial(), toMaterial, half, facing, open, powered, isWaterlogged);
                        changeset.add(trapdoorsChange);

                    }
                }
            }
        }

        undoManager.store(player, changeset);
        ChatMessageUtils.sendSuccess(commandSender, String.format("Success! %d blocks changed.", changeset.count()));
        return true;

    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.TRAPDOORS);
    }

}
