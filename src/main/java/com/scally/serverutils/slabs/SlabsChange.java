package com.scally.serverutils.slabs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;

import java.util.List;

public record SlabsChange (Location location,
                           Material beforeMaterial,
                           Material afterMaterial,
                           Slab.Type type,
                           boolean waterlogged) {}
