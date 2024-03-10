package com.scally.serverutils.bootstrap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PaulBootstrapCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        // clear inventory, armor, and offhand
        player.getInventory().clear();
        player.getEquipment().setHelmet(null);
        player.getEquipment().setChestplate(null);
        player.getEquipment().setLeggings(null);
        player.getEquipment().setBoots(null);
        player.getEquipment().setItemInOffHand(null);

        // init helmet with mending, unbreaking, protection, thorns, acqua affinity, and respiration
        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        Map<Enchantment,Integer> helmetEnchants = new HashMap<Enchantment,Integer>();
        helmetEnchants.put(Enchantment.MENDING, 1);
        helmetEnchants.put(Enchantment.DURABILITY,3);
        helmetEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL,4);
        helmetEnchants.put(Enchantment.THORNS,3);
        helmetEnchants.put(Enchantment.WATER_WORKER,1);
        helmetEnchants.put(Enchantment.OXYGEN,3);
        helmet.addEnchantments(helmetEnchants);

        // init elytra with mending and unbreaking
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        Map<Enchantment,Integer> elytraEnchants = new HashMap<Enchantment,Integer>();
        elytraEnchants.put(Enchantment.MENDING, 1);
        elytraEnchants.put(Enchantment.DURABILITY,3);
        elytra.addEnchantments(elytraEnchants);

        // init leggings with mending, unbreaking, protection, thorns, and swift sneak
        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        Map<Enchantment,Integer> leggingsEnchants = new HashMap<Enchantment,Integer>();
        leggingsEnchants.put(Enchantment.MENDING, 1);
        leggingsEnchants.put(Enchantment.DURABILITY,3);
        leggingsEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL,4);
        leggingsEnchants.put(Enchantment.THORNS,3);
        leggingsEnchants.put(Enchantment.SWIFT_SNEAK,3);
        leggings.addEnchantments(leggingsEnchants);

        // init boots with mending, unbreaking, protection, thorns, feather falling, depth strider, and soul speed
        ItemStack boots = new ItemStack(Material.NETHERITE_LEGGINGS);
        Map<Enchantment,Integer> bootsEnchants = new HashMap<Enchantment,Integer>();
        bootsEnchants.put(Enchantment.MENDING, 1);
        bootsEnchants.put(Enchantment.DURABILITY,3);
        bootsEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL,4);
        bootsEnchants.put(Enchantment.THORNS,3);
        bootsEnchants.put(Enchantment.PROTECTION_FALL,4);
        bootsEnchants.put(Enchantment.DEPTH_STRIDER,3);
        bootsEnchants.put(Enchantment.SOUL_SPEED,3);
        leggings.addEnchantments(leggingsEnchants);

        // init statues book
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        // Cast the ItemStack to BookMeta
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        // Set the title of the book
        bookMeta.setTitle("Statues");

        // Set the book meta to the ItemStack
        book.setItemMeta(bookMeta);

        // set armor and off hand
        player.getInventory().setItem(EquipmentSlot.HEAD, helmet);
        player.getInventory().setItem(EquipmentSlot.CHEST, elytra);
        player.getInventory().setItem(EquipmentSlot.LEGS, leggings);
        player.getInventory().setItem(EquipmentSlot.FEET, boots);
        player.getInventory().setItem(EquipmentSlot.OFF_HAND, new ItemStack(Material.FIREWORK_ROCKET));

        // set inventory slots
        player.getInventory().setItem(0, new ItemStack(Material.DEBUG_STICK));
        player.getInventory().setItem(1, new ItemStack(Material.WOODEN_AXE));
        player.getInventory().setItem(2, book);

        return true;
    }
}
