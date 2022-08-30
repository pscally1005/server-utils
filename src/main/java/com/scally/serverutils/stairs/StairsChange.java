package com.scally.serverutils.stairs;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public record StairsChange(Location location,
                           Material beforeMaterial,
                           Material afterMaterial,
                           Bisected.Half half,
                           BlockFace facing,
                           Stairs.Shape shape,
                           boolean waterlogged) implements Change {
    @Override
    public boolean undo() {
        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        Stairs stair = (Stairs) bd;
        stair.setHalf(half);
        stair.setFacing(facing);
        stair.setShape(shape);
        stair.setWaterlogged(waterlogged);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;
    }
}
