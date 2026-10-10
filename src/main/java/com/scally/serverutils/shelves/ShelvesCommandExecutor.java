package com.scally.serverutils.shelves;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.template.TemplateReplaceCommandExecutor;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining;
import org.bukkit.block.data.type.Shelf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShelvesCommandExecutor extends TemplateReplaceCommandExecutor<ShelvesChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .allowWorldEditSelection()
            .withFromDistribution(6, Tag.WOODEN_SHELVES)
            .withToDistribution(7, Tag.WOODEN_SHELVES)
            .build();

    public ShelvesCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<ShelvesChange> newChangeset() {
        return new ShelvesChangeset();
    }

    @Override
    public ShelvesChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            Shelf shelf = (Shelf) blockData;
            final Material fromMaterial = shelf.getMaterial();
            final BlockFace facing = shelf.getFacing();
            final SideChaining.ChainPart sideChain = shelf.getSideChain();
            final boolean powered = shelf.isPowered();
            final boolean waterlogged = shelf.isWaterlogged();
            final ItemStack[] shelfContents = snapshotShelfContents(block);

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);

            blockData = block.getBlockData();
            shelf = (Shelf) blockData;
            shelf.setFacing(facing);
            shelf.setSideChain(sideChain);
            shelf.setPowered(powered);
            shelf.setWaterlogged(waterlogged);

            location.getWorld().setBlockData(location, blockData);
            restoreShelfContents(block, shelfContents);
            return new ShelvesChange(location, fromMaterial, toMaterial, facing, sideChain, powered, waterlogged,
                    shelfContents);
        }
        return null;
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.WOODEN_SHELVES);
    }

    static ItemStack[] snapshotShelfContents(Block block) {
        final BlockState state = block.getState(false);
        if (state instanceof org.bukkit.block.Shelf shelfState) {
            final Inventory inventory = shelfState.getInventory();
            final ItemStack[] contents = inventory.getContents();
            final ItemStack[] snapshot = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                snapshot[i] = contents[i] == null ? null : contents[i].clone();
            }
            return snapshot;
        }
        return snapshotShelfContentsLegacy(block);
    }

    private static ItemStack[] snapshotShelfContentsLegacy(Block block) {
        final BlockState state = block.getState();
        if (state instanceof org.bukkit.inventory.BlockInventoryHolder holder) {
            final Inventory inventory = holder.getInventory();
            final ItemStack[] contents = inventory.getContents();
            final ItemStack[] snapshot = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                snapshot[i] = contents[i] == null ? null : contents[i].clone();
            }
            return snapshot;
        }
        return new ItemStack[0];
    }

    static void restoreShelfContents(Block block, ItemStack[] contents) {
        if (contents == null || contents.length == 0) {
            return;
        }
        final BlockState state = block.getState(false);
        if (state instanceof org.bukkit.block.Shelf shelfState) {
            final Inventory inventory = shelfState.getInventory();
            final ItemStack[] restored = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                restored[i] = contents[i] == null ? null : contents[i].clone();
            }
            inventory.setContents(restored);
            state.update(true, false);
            return;
        }
        final BlockState snapshotState = block.getState();
        if (snapshotState instanceof org.bukkit.inventory.BlockInventoryHolder holder) {
            final Inventory inventory = holder.getInventory();
            final ItemStack[] restored = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                restored[i] = contents[i] == null ? null : contents[i].clone();
            }
            inventory.setContents(restored);
            snapshotState.update(true, false);
        }
    }

}
