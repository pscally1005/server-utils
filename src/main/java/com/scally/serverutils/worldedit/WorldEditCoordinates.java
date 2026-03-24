package com.scally.serverutils.worldedit;

import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidationErrorCode;
import com.scally.serverutils.validation.InputValidationException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WorldEditCoordinates {

    private WorldEditCoordinates() {}

    public static boolean isWorldEditEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
    }

    public static Coordinates fromPlayer(Player player) {
        if (!isWorldEditEnabled()) {
            throw new InputValidationException(InputValidationErrorCode.WORLDEDIT_NOT_INSTALLED);
        }
        try {
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());
            Region region = session.getSelection(weWorld);
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            int[] coords = {
                    min.x(), min.y(), min.z(),
                    max.x(), max.y(), max.z()
            };
            return new Coordinates(coords);
        } catch (IncompleteRegionException e) {
            throw new InputValidationException(InputValidationErrorCode.WORLDEDIT_INCOMPLETE_SELECTION);
        }
    }
}
