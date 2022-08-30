package com.scally.serverutils.stairs;

import com.scally.serverutils.slabs.SlabsChange;
import com.scally.serverutils.undo.Changeset;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

import java.util.ArrayList;
import java.util.List;

public class StairsChangeset implements Changeset<StairsChange> {

    private final List<StairsChange> stairsChangeList = new ArrayList<>();
    private boolean locked = false;

    @Override
    public void add(StairsChange change) {
        if (locked) {
            throw new IllegalStateException("Changeset is already locked!");
        }
        stairsChangeList.add(change);
    }

    @Override
    public boolean undo() {

        for(StairsChange stairsChange : stairsChangeList) {

            Location location = stairsChange.location();
            Block block = location.getBlock();

            Bisected.Half half = stairsChange.half();
            BlockFace facing = stairsChange.facing();
            Stairs.Shape shape = stairsChange.shape();
            boolean isWaterlogged = stairsChange.waterlogged();
            block.setType(stairsChange.beforeMaterial(), false);

            BlockData bd = block.getBlockData();
            Stairs stair = (Stairs) bd;
            stair.setHalf(half);
            stair.setFacing(facing);
            stair.setShape(shape);
            stair.setWaterlogged(isWaterlogged);

            World world = location.getWorld();
            world.setBlockData(stairsChange.location(), bd);

        }

        return true;

    }

    @Override
    public void lock() {
        locked = true;
    }

    @Override
    public int count() {
        return stairsChangeList.size();
    }
}
