package com.scally.serverutils.slabs;

import com.scally.serverutils.undo.Changeset;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// TODO: change from <Block> to a type that has the coordinate and the Material
// TODO: try to explain generics better next time

public class SlabsChangeset implements Changeset<Block> {

    private final List<Block> beforeList = new ArrayList<>();
    private final List<Block> afterList = new ArrayList<>();

    private boolean locked = false;

    public void add(Block beforeBlock, Block afterBlock) {
        if (locked) {
            throw new IllegalStateException("Changeset is already locked!");
        }
        beforeList.add(beforeBlock);
        afterList.add(afterBlock);
    }

    public boolean undo() {

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

        return true;
    }

    public void lock() {
        locked = true;
    }

    public int count() {
        return afterList.size();
    }

    public void returnStrings(Player player) {
        String before = "BEFORE: ";
        for(int i = 0; i < beforeList.size(); i++) {
            before = before + beforeList.get(i).getBlockData().getMaterial().toString() + ", ";
        }
        before = before.substring(0, before.length()-2);
        player.sendMessage(before);

        String after = "AFTER: ";
        for(int i = 0; i < afterList.size(); i++) {
            after = after + afterList.get(i).getBlockData().getMaterial().toString() + ", ";
        }
        after = after.substring(0, after.length()-2);
        player.sendMessage(after);

    }

}
