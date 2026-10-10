package com.scally.serverutils.shelves;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;
import org.bukkit.block.data.type.Shelf;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record ShelvesChange(Location location,
                            Material beforeMaterial,
                            Material afterMaterial,
                            BlockFace facing,
                            SideChaining.ChainPart sideChain,
                            boolean powered,
                            boolean waterlogged,
                            ItemStack[] contents) implements Change {

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

        if (contents != null && contents.length > 0) {
            final BlockState state = block.getState();
            if (state instanceof BlockInventoryHolder holder) {
                final Inventory inventory = holder.getInventory();
                final ItemStack[] restored = new ItemStack[contents.length];
                for (int i = 0; i < contents.length; i++) {
                    restored[i] = contents[i] == null ? null : contents[i].clone();
                }
                inventory.setContents(restored);
                state.update(true, false);
            }
        }

        return true;

    }

}
