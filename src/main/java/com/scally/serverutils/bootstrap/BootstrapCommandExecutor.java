package com.scally.serverutils.bootstrap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BootstrapCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        player.getInventory().clear();
        player.getEquipment().setChestplate(null);
        player.getEquipment().setItemInOffHand(null);

        player.getInventory().setItem(EquipmentSlot.CHEST, new ItemStack(Material.ELYTRA));
        player.getInventory().setItem(EquipmentSlot.OFF_HAND, new ItemStack(Material.FIREWORK_ROCKET));

        player.getInventory().setItem(0, new ItemStack(Material.DEBUG_STICK));
        player.getInventory().setItem(1, new ItemStack(Material.WOODEN_AXE));

        return true;
    }
}
