package com.scally.serverutils.distribution;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zoglin;
import org.bukkit.util.RayTraceResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DistributionTabCompleterTest {

    private static class TestTabCompleter implements DistributionTabCompleter {
        @Override
        public List<String> onTabCompleteDistribution(String arg) {
            return onTabCompleteDistribution(arg, Tag.LEAVES);
        }
    }

    private static final TestTabCompleter testTabCompleter = new TestTabCompleter();

    @Mock
    private Player player;

    @Mock
    private Command command;

    @Mock
    private RayTraceResult rayTraceResult;

    @Mock
    private Block hitBlock;

    @Mock
    private Location hitBlockLocation;

    private ServerMock serverMock;

    @BeforeEach
    public void before() {
        serverMock = MockBukkit.mock();
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    public void onTabComplete_notPlayer_returnsEmptyList() {
        final Zoglin zoglin = Mockito.mock(Zoglin.class);
        final List<String> result = testTabCompleter.onTabComplete(zoglin, command, "test", null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 8})
    public void onTabComplete_suggestsDistribution(int input) {
        final String[] args = argsOfLength(input);
        args[args.length - 1] = "oak_";
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("oak_leaves", result.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4})
    public void onTabComplete_relativeCoordinates_threeOptions(int input) {
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("~", result.get(0));
        assertEquals("~ ~", result.get(1));
        assertEquals("~ ~ ~", result.get(2));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5})
    public void onTabComplete_relativeCoordinates_twoOptions(int input) {
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("~", result.get(0));
        assertEquals("~ ~", result.get(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 6})
    public void onTabComplete_relativeCoordinates_oneOption(int input) {
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("~", result.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7})
    public void onTabComplete_relativeCoordinates_invalidArgLength(int input) {
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void onTabComplete_hitBlockNull_returnsEmptyList() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble())).thenReturn(rayTraceResult);
        final String[] args = argsOfLength(5);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4})
    public void onTabComplete_absoluteCoordinates_threeOptions(int input) {
        mockAbsoluteCoordinates();
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("100", result.get(0));
        assertEquals("100 200", result.get(1));
        assertEquals("100 200 300", result.get(2));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5})
    public void onTabComplete_absoluteCoordinates_twoOptions(int input) {
        mockAbsoluteCoordinates();
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("200", result.get(0));
        assertEquals("200 300", result.get(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 6})
    public void onTabComplete_absoluteCoordinates_oneOption(int input) {
        mockAbsoluteCoordinates();
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("300", result.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 9})
    public void onTabComplete_absoluteCoordinates_invalidArgLength(int input) {
        mockAbsoluteCoordinates();
        final String[] args = argsOfLength(input);
        final List<String> result = testTabCompleter.onTabComplete(player, command, "test", args);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void onTabCompleteDistribution_emptyString_returnsFullList() {
        final List<String> result = testTabCompleter.onTabCompleteDistribution("");

        assertNotNull(result);
        assertContainsAllLeaves(result);
    }

    @Test
    public void onTabCompleteDistribution_noCommaOrPercent_filterMatchesNone_returnsEmptyList() {
        final List<String> result = testTabCompleter.onTabCompleteDistribution("5");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void onTabCompleteDistribution_noCommaOrPercent_filterMatchesSome_returnsFilteredList() {
        final List<String> result = testTabCompleter.onTabCompleteDistribution("oak_l");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("oak_leaves", result.get(0));
    }

    @Test
    public void onTabCompleteDistribution_percentMoreRecent_singleMaterialDistribution() {
        final List<String> result = testTabCompleter.onTabCompleteDistribution("50%s");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("50%spruce_leaves", result.get(0));
    }

    @Test
    public void onTabCompleteDistribution_percentMoreRecent_multiMaterialDistribution() {
        final String arg = "50%spruce_leaves,40%";
        final List<String> result = testTabCompleter.onTabCompleteDistribution(arg);

        assertNotNull(result);
        assertContainsAllLeaves(result, arg);
    }

    @Test
    public void onTabCompleteDistribution_commaMoreRecent_multiMaterialDistribution() {
        final String arg = "oak_leaves,a";
        final List<String> result = testTabCompleter.onTabCompleteDistribution(arg);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("oak_leaves,acacia_leaves"));
        assertTrue(result.contains("oak_leaves,azalea_leaves"));
    }

    private String[] argsOfLength(int length) {
        final String[] args = new String[length];
        for (int i = 0; i < length; i++) {
           args[i] = String.valueOf(length);
        }
        return args;
    }

    private void mockAbsoluteCoordinates() {
        Mockito.when(player.rayTraceBlocks(Mockito.anyDouble()))
                .thenReturn(rayTraceResult);
        Mockito.when(rayTraceResult.getHitBlock()).thenReturn(hitBlock);
        Mockito.when(hitBlock.getLocation()).thenReturn(hitBlockLocation);
        Mockito.when(hitBlockLocation.getBlockX()).thenReturn(100);
        Mockito.when(hitBlockLocation.getBlockY()).thenReturn(200);
        Mockito.when(hitBlockLocation.getBlockZ()).thenReturn(300);
    }

    private void assertContainsAllLeaves(List<String> result) {
        assertContainsAllLeaves(result, "");
    }

    private void assertContainsAllLeaves(List<String> result, String prefix) {
        assertEquals(Tag.LEAVES.getValues().size(), result.size());
        for (Material leaves : Tag.LEAVES.getValues()) {
            assertTrue(result.contains(prefix + leaves.toString().toLowerCase()));
        }
    }
}
