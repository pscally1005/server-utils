package com.scally.serverutils.shelves;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;
import org.bukkit.block.data.type.Shelf;

public record ShelvesChange(Location location,
                            Material beforeMaterial,
                            Material afterMaterial,
                            BlockFace facing,
                            SideChaining.ChainPart sideChain,
                            boolean powered,
                            boolean waterlogged) implements Change {

    @Override
    public boolean undo() {

        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        Shelf shelf = (Shelf) bd;
        shelf.setFacing(facing);
        shelf.setSideChain(sideChain);
        shelf.setPowered(powered);
        shelf.setWaterlogged(waterlogged);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;

    }

}
