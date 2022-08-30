package com.scally.serverutils.stairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;

public record StairsChange(Location location,
                           Material before,
                           Material after,
                           Bisected.Half half,
                           Directional facing,
                           Stairs.Shape shape,
                           boolean waterlogged) {}
