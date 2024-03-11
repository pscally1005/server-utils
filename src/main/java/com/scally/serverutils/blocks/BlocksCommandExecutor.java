package com.scally.serverutils.blocks;

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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BlocksCommandExecutor extends TemplateReplaceCommandExecutor<BlocksChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
//            .withFromDistribution(6, null)
//            .withToDistribution(7, null)
            .build();

    public BlocksCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<BlocksChange> newChangeset() {
        return new BlocksChangeset();
    }

    @Override
    public BlocksChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
//            Stairs stairs = (Stairs) blockData;
            final Material fromMaterial = blockData.getMaterial();
//            final Bisected.Half half = blockData.getHalf();
//            final BlockFace facing = blockData.getFacing();
//            final Stairs.Shape shape = blockData.getShape();
//            final boolean waterlogged = blockData.isWaterlogged();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);

            blockData = block.getBlockData();
//            blockData = (Stairs) blockData;
//            blockData.setHalf(half);
//            blockData.setFacing(facing);
//            blockData.setShape(shape);
//            blockData.setWaterlogged(waterlogged);

            location.getWorld().setBlockData(location, blockData);
            return new BlocksChange(location, fromMaterial, toMaterial/*, half, facing, shape, waterlogged*/);
        }
        return null;
    }

    public List<String> onTabCompleteDistribution(String arg) {
        List<Material> materials = Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList());
        List<String> mats = new ArrayList<String>();
        for(Material mat : materials) {
            mats.add(mat.toString().toLowerCase());
        }
        return mats;
    }

}