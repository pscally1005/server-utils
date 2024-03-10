package com.scally.serverutils;

import com.scally.serverutils.bootstrap.BootstrapCommandExecutor;
import com.scally.serverutils.bootstrap.PaulBootstrapCommandExecutor;
import com.scally.serverutils.fillcontainer.FillContainerCommandExecutor;
import com.scally.serverutils.slabs.SlabsCommandExecutor;
import com.scally.serverutils.stairs.StairsCommandExecutor;
import com.scally.serverutils.trapdoors.TrapDoorsCommandExecutor;
import com.scally.serverutils.undo.UndoCommandExecutor;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.walls.WallsCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils extends JavaPlugin {

    public static final int VOLUME_LIMIT = 64 * 64 * 64;

    private final UndoManager undoManager = UndoManager.getInstance();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("bootstrap").setExecutor(new BootstrapCommandExecutor());
        this.getCommand("paul").setExecutor(new PaulBootstrapCommandExecutor());
        this.getCommand("slabs").setExecutor(new SlabsCommandExecutor(undoManager));
        this.getCommand("stairs").setExecutor(new StairsCommandExecutor(undoManager));
        this.getCommand("fill-container").setExecutor(new FillContainerCommandExecutor());
        this.getCommand("s-undo").setExecutor(new UndoCommandExecutor(undoManager));
        this.getCommand("trapdoors").setExecutor(new TrapDoorsCommandExecutor(undoManager));
        this.getCommand("walls").setExecutor(new WallsCommandExecutor(undoManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
