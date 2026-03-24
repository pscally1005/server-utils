package com.scally.serverutils.logs;

import com.scally.serverutils.distribution.Distribution;
import com.scally.serverutils.template.TemplateReplaceCommandExecutor;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

import java.util.List;

public class LogsCommandExecutor extends TemplateReplaceCommandExecutor<LogsChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .allowWorldEditSelection()
            .withFromDistribution(6, Tag.LOGS)
            .withToDistribution(7, Tag.LOGS)
            .build();

    public LogsCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<LogsChange> newChangeset() {
        return new LogsChangeset();
    }

    @Override
    public LogsChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        // Check if block is Orientable (all logs/wood are Orientable)
        if (!(blockData instanceof Orientable)) {
            return null;
        }

        final Distribution fromDistribution = validationResult.fromDistribution();
        final Distribution toDistribution = validationResult.toDistribution();

        // Check if this block's exact material is in the from distribution
        if (!fromDistribution.hasMaterial(material)) {
            return null;
        }

        Orientable orientable = (Orientable) blockData;
        final Material fromMaterial = orientable.getMaterial();
        final Axis axis = orientable.getAxis();

        // Pick the exact material from to distribution (handles randomness if multiple)
        Material toMaterial = toDistribution.pick();
        if (toMaterial == null) {
            return null;
        }

        // Set to the exact material specified, only preserving the axis
        block.setType(toMaterial, false);
        blockData = block.getBlockData();

        orientable = (Orientable) blockData;
        orientable.setAxis(axis);
        location.getWorld().setBlockData(location, blockData);

        return new LogsChange(location, fromMaterial, toMaterial, axis);
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.LOGS);
    }

}
