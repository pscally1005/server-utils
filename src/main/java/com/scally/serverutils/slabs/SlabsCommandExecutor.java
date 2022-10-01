package com.scally.serverutils.slabs;

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
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;

import java.util.List;

// TODO: unit tests

public class SlabsCommandExecutor extends TemplateReplaceCommandExecutor<SlabsChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .withFromDistribution(6, Tag.SLABS)
            .withToDistribution(7, Tag.SLABS)
            .build();

    private final SlabsChangeset changeset = new SlabsChangeset();

    public SlabsCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<SlabsChange> changeset() {
        return changeset;
    }

    @Override
    protected SlabsChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            Slab slab = (Slab) blockData;
            final Material fromMaterial = slab.getMaterial();
            final Slab.Type type = slab.getType();
            final boolean isWaterlogged = slab.isWaterlogged();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);
            blockData = block.getBlockData();

            slab = (Slab) blockData;
            slab.setWaterlogged(isWaterlogged);
            slab.setType(type);
            location.getWorld().setBlockData(location, blockData);

            return new SlabsChange(location, fromMaterial, toMaterial, type, isWaterlogged);
        }
        return null;
    }

    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.SLABS);
    }

}
