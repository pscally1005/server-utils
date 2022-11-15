package com.scally.serverutils.undo;

import com.scally.serverutils.slabs.SlabsChange;
import com.scally.serverutils.slabs.SlabsChangeset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.Slab;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChangesetListTest {

    @Mock
    private Location location;

    @Test
    void addTest_locked() {

        Material fromMaterial = Material.COBBLESTONE_SLAB;
        Material toMaterial = Material.BIRCH_SLAB;
        Slab.Type type = Slab.Type.TOP;
        boolean isWaterlogged = false;
        SlabsChange slabsChange = new SlabsChange(location, fromMaterial, toMaterial, type, isWaterlogged);

        SlabsChangeset slabsChangeset = new SlabsChangeset();
        slabsChangeset.lock();
        assertThrowsExactly(IllegalStateException.class, () -> slabsChangeset.add(slabsChange));

    }

    @Test
    void addTest_unlocked() {

        Material fromMaterial = Material.COBBLESTONE_SLAB;
        Material toMaterial = Material.BIRCH_SLAB;
        Slab.Type type = Slab.Type.TOP;
        boolean isWaterlogged = false;
        SlabsChange slabsChange = new SlabsChange(location, fromMaterial, toMaterial, type, isWaterlogged);

        SlabsChangeset slabsChangeset = new SlabsChangeset();
        assertDoesNotThrow(() -> slabsChangeset.add(slabsChange));

    }

    @Test
    void undoTest_true() {
        SlabsChangeset slabsChangeset = new SlabsChangeset();

        Change mockChange1 = Mockito.mock(Change.class);
        Mockito.when(mockChange1.undo()).thenReturn(true);

        slabsChangeset.add(mockChange1);
        assertTrue(slabsChangeset.undo());
    }

    @Test
    void undoTest_false() {
        SlabsChangeset slabsChangeset = new SlabsChangeset();

        Change mockChange2 = Mockito.mock(Change.class);
        Mockito.when(mockChange2.undo()).thenReturn(false);

        slabsChangeset.add(mockChange2);
        assertFalse(slabsChangeset.undo());
    }

    @Test
    void lockTest() {
        SlabsChangeset slabsChangeset = new SlabsChangeset();
        assertFalse(slabsChangeset.isLocked());
        slabsChangeset.lock();
        assertTrue(slabsChangeset.isLocked());
    }

    @Test
    void countTest() {
        SlabsChangeset slabsChangeset = new SlabsChangeset();
        assertEquals(0, slabsChangeset.count());

        Location location = null;
        Material fromMaterial = Material.COBBLESTONE_SLAB;
        Material toMaterial = Material.BIRCH_SLAB;
        Slab.Type type = Slab.Type.TOP;
        boolean isWaterlogged = false;
        SlabsChange slabsChange = new SlabsChange(location, fromMaterial, toMaterial, type, isWaterlogged);

        if(!slabsChangeset.isLocked()) {
            slabsChangeset.add(slabsChange);
            assertEquals(1, slabsChangeset.count());
        }
    }

}
