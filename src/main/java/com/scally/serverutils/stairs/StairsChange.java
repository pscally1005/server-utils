package com.scally.serverutils.stairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Stairs;

public record StairsChange(Location location,
                           Material beforeMaterial,
                           Material afterMaterial,
                           Bisected.Half half,
                           BlockFace facing,
                           Stairs.Shape shape,
                           boolean waterlogged) {}
