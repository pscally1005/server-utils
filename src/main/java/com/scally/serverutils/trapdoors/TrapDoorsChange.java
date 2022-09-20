package com.scally.serverutils.trapdoors;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;

public record TrapDoorsChange(Location location,
                              Material beforeMaterial,
                              Material afterMaterial,
                              Bisected.Half half,
                              BlockFace facing,
                              boolean open,
                              boolean powered,
                              boolean waterlogged) implements Change {

    @Override
    public boolean undo() {

        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        TrapDoor trapdoor = (TrapDoor) bd;
        trapdoor.setHalf(half);
        trapdoor.setFacing(facing);
        trapdoor.setOpen(open);
        trapdoor.setPowered(powered);
        trapdoor.setWaterlogged(waterlogged);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;

    }

}
