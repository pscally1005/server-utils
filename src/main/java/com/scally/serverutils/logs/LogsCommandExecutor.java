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

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            Orientable orientable = (Orientable) blockData;
            final Material fromMaterial = orientable.getMaterial();
            final Axis axis = orientable.getAxis();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);
            blockData = block.getBlockData();

            orientable = (Orientable) blockData;
            orientable.setAxis(axis);
            location.getWorld().setBlockData(location, blockData);

            return new LogsChange(location, fromMaterial, toMaterial, axis);
        }
        return null;
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.LOGS);
    }

}
