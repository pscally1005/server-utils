package com.scally.serverutils.undo;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;

import java.util.ArrayList;
import java.util.List;

public class Changeset {

    private final List<Block> beforeList = new ArrayList<>();
    private final List<Block> afterList = new ArrayList<>();

    public void store(Block beforeBlock, Block afterBlock) {
        beforeList.add(beforeBlock);
        afterList.add(afterBlock);
    }

    public void undo() {

        int changedCount = 0;
        for(int i = beforeList.size()-1; i >= 0; i--) {

            /*Block block = afterList.get(i);
            BlockData bd = block.getBlockData();
            Slab slab = (Slab) bd;
            Slab.Type type = slab.getType();
            boolean isWaterlogged = slab.isWaterlogged();
            Material toSlab = toDistribution.pick();
            block.setType(toSlab, false);
            bd = block.getBlockData();
            ((Slab) bd).setWaterlogged(isWaterlogged);
            ((Slab) bd).setType(type);
            world.setBlockData(block.getX(), block.getY(), block.getZ(), bd);
            changedCount++;*/

        }
    }

}
