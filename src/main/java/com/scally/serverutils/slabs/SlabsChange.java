package com.scally.serverutils.slabs;

import org.bukkit.Location;
import org.bukkit.Material;

public record SlabsChange (Location location, Material beforeMaterial, Material afterMaterial) {}
