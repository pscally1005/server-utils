package com.scally.serverutils.slabs;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;

public record SlabsChange (Location location,
                           Material beforeMaterial,
                           Material afterMaterial,
                           Slab.Type type,
                           boolean waterlogged) implements Change {
    @Override
    public boolean undo() {
        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        Slab slab = (Slab) bd;
        slab.setWaterlogged(waterlogged);
        slab.setType(type);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;
    }
}
