package com.scally.serverutils.fillcontainer;

import com.scally.serverutils.distribution.Distribution;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;

import java.util.Set;

public class ContainerUtils {

    private static final Set<Material> ALLOWED_MATERIALS = Set.of(
            Material.CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            // Shelf blocks
            Material.OAK_SHELF,
            Material.SPRUCE_SHELF,
            Material.BIRCH_SHELF,
            Material.JUNGLE_SHELF,
            Material.ACACIA_SHELF,
            Material.DARK_OAK_SHELF,
            Material.MANGROVE_SHELF,
            Material.CHERRY_SHELF,
            Material.PALE_OAK_SHELF,
            Material.BAMBOO_SHELF,
            Material.CRIMSON_SHELF,
            Material.WARPED_SHELF
    );

    /**
     * Returns the set of allowed container materials.
     * @return Set of allowed Material types
     */
    public static Set<Material> getAllowedMaterials() {
        return ALLOWED_MATERIALS;
    }

    /**
     * Checks if the given block type is an allowed container material.
     * @param material The material to check
     * @return true if the material is allowed, false otherwise
     */
    public static boolean isAllowedMaterial(Material material) {
        return ALLOWED_MATERIALS.contains(material);
    }

    /**
     * Fills a container block with the given distribution.
     * Handles regular containers (chests, barrels, shulker boxes) and shelf blocks.
     * 
     * @param block The block to fill
     * @param distribution The distribution to use for filling
     * @return true if the block was successfully filled, false otherwise
     */
    public static boolean fillContainer(Block block, Distribution distribution) {
        if (!isAllowedMaterial(block.getType())) {
            return false;
        }

        Inventory inventory;
        
        // Regular containers (chests, barrels, shulker boxes) use Container interface
        // Shelf blocks use BlockInventoryHolder interface
        // Since Container extends BlockInventoryHolder, check Container first
        if (block.getState() instanceof Container container) {
            inventory = container.getInventory();
        } else if (block.getState() instanceof BlockInventoryHolder holder) {
            inventory = holder.getInventory();
        } else {
            return false;
        }

        distribution.fill(inventory);
        return true;
    }
}
