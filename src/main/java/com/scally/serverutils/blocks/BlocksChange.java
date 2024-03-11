package com.scally.serverutils.blocks;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public record BlocksChange(Location location,
                           Material beforeMaterial,
                           Material afterMaterial/*,
                           BlockFace facing,
                           boolean waterlogged*/) implements Change {
    @Override
    public boolean undo() {
        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
//        Block block = (Block) bd;
//        block.setFacing(facing);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;
    }
}