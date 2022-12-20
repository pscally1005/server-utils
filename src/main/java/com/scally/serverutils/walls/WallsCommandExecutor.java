package com.scally.serverutils.walls;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.template.TemplateReplaceCommandExecutor;
import com.scally.serverutils.walls.WallsChange;
import com.scally.serverutils.walls.WallsChangeset;
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
import org.bukkit.block.data.type.Wall;

import java.util.List;

public class WallsCommandExecutor extends TemplateReplaceCommandExecutor<WallsChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .withFromDistribution(6, Tag.WALLS)
            .withToDistribution(7, Tag.WALLS)
            .build();

    public WallsCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<WallsChange> newChangeset() {
        return new WallsChangeset();
    }

    @Override
    public WallsChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            Wall wall = (Wall) blockData;
            final Material fromMaterial = wall.getMaterial();
            final Wall.Height eastHeight = wall.getHeight(BlockFace.EAST);
            final Wall.Height westHeight = wall.getHeight(BlockFace.WEST);
            final Wall.Height northHeight = wall.getHeight(BlockFace.NORTH);
            final Wall.Height southHeight = wall.getHeight(BlockFace.SOUTH);
            final boolean isUp = wall.isUp();
            final boolean isWaterlogged = wall.isWaterlogged();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);

            blockData = block.getBlockData();
            wall = (Wall) blockData;
            wall.setHeight(BlockFace.EAST, eastHeight);
            wall.setHeight(BlockFace.WEST, westHeight);
            wall.setHeight(BlockFace.NORTH, northHeight);
            wall.setHeight(BlockFace.SOUTH, southHeight);
            wall.setUp(isUp);
            wall.setWaterlogged(isWaterlogged);

            location.getWorld().setBlockData(location, blockData);
            return new WallsChange(location, fromMaterial, toMaterial, eastHeight, westHeight, northHeight, southHeight, isUp, isWaterlogged);
        }
        return null;
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.TRAPDOORS);
    }

}
