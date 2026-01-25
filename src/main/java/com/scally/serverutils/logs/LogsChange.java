package com.scally.serverutils.logs;

import com.scally.serverutils.undo.Change;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

public record LogsChange(Location location,
                        Material beforeMaterial,
                        Material afterMaterial,
                        Axis axis) implements Change {
    @Override
    public boolean undo() {
        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        Orientable orientable = (Orientable) bd;
        orientable.setAxis(axis);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;
    }
}
