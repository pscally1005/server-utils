package com.scally.serverutils.tabcompleter;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class TabCompleteCoordinateTest {

    @MockBukkitInject
    private ServerMock serverMock;

    private World world;
    private Location location;

    @BeforeEach
    void beforeEach() {
        world = serverMock.addSimpleWorld("test");
        location = new Location(world, 1, 2, 3);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0,UNKNOWN",
            "1,X",
            "2,Y",
            "3,Z",
            "4,UNKNOWN"
    })
    void test_forOneCoordinateCommand(int argsLength, TabCompleteCoordinate expected) {
        final TabCompleteCoordinate actual = TabCompleteCoordinate.forOneCoordinateCommand(argsLength);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "0,UNKNOWN",
            "1,X",
            "2,Y",
            "3,Z",
            "4,X",
            "5,Y",
            "6,Z",
            "7,UNKNOWN"
    })
    void test_forTwoCoordinateCommand(int argsLength, TabCompleteCoordinate expected) {
        final TabCompleteCoordinate actual = TabCompleteCoordinate.forTwoCoordinateCommand(argsLength);
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteAbsoluteCoordinates_X() {
        final List<String> expected = List.of(
                "1", "1 2", "1 2 3"
        );

        final List<String> actual = TabCompleteCoordinate.X.getTabCompleteAbsoluteCoordinates(location);
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteAbsoluteCoordinates_Y() {
        final List<String> expected = List.of(
                "2", "2 3"
        );

        final List<String> actual = TabCompleteCoordinate.Y.getTabCompleteAbsoluteCoordinates(location);
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteAbsoluteCoordinates_Z() {
        final List<String> expected = List.of("3");

        final List<String> actual = TabCompleteCoordinate.Z.getTabCompleteAbsoluteCoordinates(location);
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteRelativeCoordinates_X() {
        final List<String> expected = List.of("~", "~ ~", "~ ~ ~");
        final List<String> actual = TabCompleteCoordinate.X.getTabCompleteRelativeCoordinates();
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteRelativeCoordinates_Y() {
        final List<String> expected = List.of("~", "~ ~");
        final List<String> actual = TabCompleteCoordinate.Y.getTabCompleteRelativeCoordinates();
        assertEquals(expected, actual);
    }

    @Test
    void test_getTabCompleteRelativeCoordinates_Z() {
        final List<String> expected = List.of("~");
        final List<String> actual = TabCompleteCoordinate.Z.getTabCompleteRelativeCoordinates();
        assertEquals(expected, actual);
    }
}
