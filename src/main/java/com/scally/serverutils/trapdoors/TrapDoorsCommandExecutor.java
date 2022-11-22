package com.scally.serverutils.trapdoors;

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
import org.bukkit.block.data.type.TrapDoor;

import java.util.List;

public class TrapDoorsCommandExecutor extends TemplateReplaceCommandExecutor<TrapDoorsChange> {

    private final InputValidator inputValidator = InputValidator.builder()
            .expectedNumArgs(8)
            .playerOnly()
            .withCoordinateValidation()
            .withFromDistribution(6, Tag.TRAPDOORS)
            .withToDistribution(7, Tag.TRAPDOORS)
            .build();

    public TrapDoorsCommandExecutor(UndoManager undoManager) {
        super(undoManager);
    }

    @Override
    protected InputValidator inputValidator() {
        return inputValidator;
    }

    @Override
    protected Changeset<TrapDoorsChange> newChangeset() {
        return new TrapDoorsChangeset();
    }

    @Override
    public TrapDoorsChange changeAtLocation(Location location, ValidationResult validationResult) {
        final Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        final Material material = blockData.getMaterial();

        final Distribution fromDistribution = validationResult.fromDistribution();
        if (fromDistribution.hasMaterial(material)) {
            TrapDoor trapDoor = (TrapDoor) blockData;
            final Material fromMaterial = trapDoor.getMaterial();
            final Bisected.Half half = trapDoor.getHalf();
            final BlockFace facing = trapDoor.getFacing();
            final boolean open = trapDoor.isOpen();
            final boolean powered = trapDoor.isPowered();
            final boolean waterlogged = trapDoor.isWaterlogged();

            final Distribution toDistribution = validationResult.toDistribution();
            final Material toMaterial = toDistribution.pick();
            block.setType(toMaterial, false);

            blockData = block.getBlockData();
            trapDoor = (TrapDoor) blockData;
            trapDoor.setHalf(half);
            trapDoor.setFacing(facing);
            trapDoor.setOpen(open);
            trapDoor.setPowered(powered);
            trapDoor.setWaterlogged(waterlogged);

            location.getWorld().setBlockData(location, blockData);
            return new TrapDoorsChange(location, fromMaterial, toMaterial, half, facing, open, powered, waterlogged);
        }
        return null;
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.TRAPDOORS);
    }

}
