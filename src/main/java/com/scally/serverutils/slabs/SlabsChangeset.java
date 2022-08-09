package com.scally.serverutils.slabs;

import com.scally.serverutils.undo.Changeset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SlabsChangeset implements Changeset<SlabsChange> {

    private final List<SlabsChange> slabsChangeList = new ArrayList<>();

    private boolean locked = false;

    public void add(SlabsChange slabsChange) {
        if (locked) {
            throw new IllegalStateException("Changeset is already locked!");
        }
        slabsChangeList.add(slabsChange);
    }

    public String undo() {

        for(SlabsChange slabsChange : slabsChangeList) {

            Location location = slabsChange.location();
            Block block = location.getBlock();

            Slab.Type type = slabsChange.type();
            boolean isWaterlogged = slabsChange.waterlogged();
            block.setType(slabsChange.beforeMaterial(), false);

            BlockData bd = block.getBlockData();
            Slab slab = (Slab) bd;
            slab.setWaterlogged(isWaterlogged);
            slab.setType(type);

            World world = location.getWorld();
            world.setBlockData(slabsChange.location(), bd);

        }

        String message = String.format("%d blocks changed back.", count());
        return message;

    }

    public void lock() {
        locked = true;
    }

    public int count() {
        return slabsChangeList.size();
    }

}
