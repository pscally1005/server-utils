package com.scally.serverutils.walls;

import com.scally.serverutils.undo.Change;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Wall;

public record WallsChange(Location location,
                          Material beforeMaterial,
                          Material afterMaterial,
                          Wall.Height eastHeight,
                          Wall.Height westHeight,
                          Wall.Height northHeight,
                          Wall.Height southHeight,
                          boolean isUp,
                          boolean isWaterlogged) implements Change {


    @Override
    public boolean undo() {

        Block block = location.getBlock();

        block.setType(beforeMaterial, false);

        BlockData bd = block.getBlockData();
        Wall wall = (Wall) bd;
        wall.setHeight(BlockFace.EAST, eastHeight);
        wall.setHeight(BlockFace.WEST, westHeight);
        wall.setHeight(BlockFace.NORTH, northHeight);
        wall.setHeight(BlockFace.SOUTH, southHeight);
        wall.setUp(isUp);
        wall.setWaterlogged(isWaterlogged);

        World world = location.getWorld();
        world.setBlockData(location, bd);

        return true;

    }

}
