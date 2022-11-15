package com.scally.serverutils.stairs;

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
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

import java.util.List;

public class StairsCommandExecutor extends TemplateReplaceCommandExecutor<StairsChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .withFromDistribution(6, Tag.STAIRS)
            .withToDistribution(7, Tag.STAIRS)
            .build();

    public StairsCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<StairsChange> changeset() {
        return new StairsChangeset();
    }

    @Override
    public StairsChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            Stairs stairs = (Stairs) blockData;
            final Material fromMaterial = stairs.getMaterial();
            final Bisected.Half half = stairs.getHalf();
            final BlockFace facing = stairs.getFacing();
            final Stairs.Shape shape = stairs.getShape();
            final boolean waterlogged = stairs.isWaterlogged();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);

            blockData = block.getBlockData();
            stairs = (Stairs) blockData;
            stairs.setHalf(half);
            stairs.setFacing(facing);
            stairs.setShape(shape);
            stairs.setWaterlogged(waterlogged);

            location.getWorld().setBlockData(location, blockData);
            return new StairsChange(location, fromMaterial, toMaterial, half, facing, shape, waterlogged);
        }
        return null;
    }

    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.STAIRS);
    }

}
