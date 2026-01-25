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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Check if block is Orientable (all logs/wood are Orientable)
        if (!(blockData instanceof Orientable)) {
            return null;
        }

        final Distribution fromDistribution = validationResult.fromDistribution();
        final Distribution toDistribution = validationResult.toDistribution();

        // Extract wood types from from distribution
        Set<String> fromWoodTypes = extractWoodTypes(fromDistribution);
        
        // Check if this block's material belongs to any of the from wood types
        String blockWoodType = extractWoodTypeFromMaterial(material);
        if (blockWoodType == null || !fromWoodTypes.contains(blockWoodType)) {
            return null;
        }

        Orientable orientable = (Orientable) blockData;
        final Material fromMaterial = orientable.getMaterial();
        final Axis axis = orientable.getAxis();

        // Pick a material from to distribution (handles randomness if multiple)
        Material pickedToMaterial = toDistribution.pick();
        String targetWoodType = extractWoodTypeFromMaterial(pickedToMaterial);
        if (targetWoodType == null) {
            return null;
        }

        // Map the material to the corresponding variant in the target wood type
        Material toMaterial = mapMaterialToWoodType(fromMaterial, blockWoodType, targetWoodType);
        if (toMaterial == null) {
            return null;
        }

        block.setType(toMaterial, false);
        blockData = block.getBlockData();

        orientable = (Orientable) blockData;
        orientable.setAxis(axis);
        location.getWorld().setBlockData(location, blockData);

        return new LogsChange(location, fromMaterial, toMaterial, axis);
    }

    /**
     * Extracts wood types from a distribution (e.g., "OAK" from "OAK_LOG")
     */
    private Set<String> extractWoodTypes(Distribution distribution) {
        Set<String> woodTypes = new HashSet<>();
        for (var item : distribution.getMaterials()) {
            String woodType = extractWoodTypeFromMaterial(item.material());
            if (woodType != null) {
                woodTypes.add(woodType);
            }
        }
        return woodTypes;
    }

    /**
     * Extracts wood type from a material name.
     * Examples: "OAK_LOG" -> "OAK", "STRIPPED_BIRCH_WOOD" -> "BIRCH"
     */
    private String extractWoodTypeFromMaterial(Material material) {
        if (material == null) return null;
        String name = material.name();
        
        // Handle stripped variants: STRIPPED_OAK_LOG -> OAK
        if (name.startsWith("STRIPPED_")) {
            name = name.substring("STRIPPED_".length());
        }
        
        // Extract wood type: OAK_LOG -> OAK, BIRCH_WOOD -> BIRCH
        if (name.endsWith("_LOG")) {
            return name.substring(0, name.length() - "_LOG".length());
        } else if (name.endsWith("_WOOD")) {
            return name.substring(0, name.length() - "_WOOD".length());
        }
        
        return null;
    }

    /**
     * Maps a material from one wood type to another, preserving the variant.
     * Examples: OAK_LOG + OAK + BIRCH -> BIRCH_LOG
     *          STRIPPED_OAK_WOOD + OAK + BIRCH -> STRIPPED_BIRCH_WOOD
     */
    private Material mapMaterialToWoodType(Material fromMaterial, String fromWoodType, String toWoodType) {
        if (fromMaterial == null || fromWoodType == null || toWoodType == null) {
            return null;
        }
        
        String materialName = fromMaterial.name();
        boolean isStripped = materialName.startsWith("STRIPPED_");
        boolean isWood = materialName.endsWith("_WOOD");
        boolean isLog = materialName.endsWith("_LOG");
        
        // Build the target material name
        StringBuilder targetName = new StringBuilder();
        if (isStripped) {
            targetName.append("STRIPPED_");
        }
        targetName.append(toWoodType);
        if (isLog) {
            targetName.append("_LOG");
        } else if (isWood) {
            targetName.append("_WOOD");
        } else {
            return null; // Unknown variant
        }
        
        return Material.matchMaterial(targetName.toString());
    }

    @Override
    public List<String> onTabCompleteDistribution(String arg) {
        return onTabCompleteDistribution(arg, Tag.LOGS);
    }

}
