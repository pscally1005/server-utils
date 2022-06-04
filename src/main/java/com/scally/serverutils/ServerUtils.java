package com.scally.serverutils;

import com.scally.serverutils.executors.FillContainerExecutor;
import com.scally.serverutils.executors.SlabsCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("slabs").setExecutor(new SlabsCommandExecutor());
        this.getCommand("fill-container").setExecutor(new FillContainerExecutor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
