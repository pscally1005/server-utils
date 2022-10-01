package com.scally.serverutils.validation;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    private ServerMock server;
    private AutoCloseable mocks;

    @Mock
    private Player player;

    @Mock
    private AbstractHorse abstractHorse;

    @BeforeEach
    public void before() {
        mocks = MockitoAnnotations.openMocks(this);
        server = MockBukkit.mock();
    }

    @AfterEach
    public void after() {
        MockBukkit.unmock();
    }

    @Test
    public void validate_happyPath() {
        final InputValidator inputValidator = InputValidator.builder()
                .expectedNumArgs(8)
                .playerOnly()
                .withCoordinateValidation()
                .withFromDistribution(6, Tag.SLABS)
                .withToDistribution(7, Tag.SLABS)
                .build();

        final String[] args = new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "oak_slab",
                "birch_slab,jungle_slab"
        };

        final Player mockPlayer = Mockito.mock(Player.class);
        final ValidationResult result = inputValidator.validate(mockPlayer, args);

        assertTrue(result.validated());
        assertNotNull(result.coordinates());
        assertNotNull(result.fromDistribution());
        assertNotNull(result.toDistribution());
    }

    @Test
    public void validateArgsNumber_correctArgs_happyPath() {
        final InputValidator validator = InputValidator.builder()
                .expectedNumArgs(3)
                .build();
        final String[] input = {"a", "b", "c"};

        validator.validateArgsNumber(input);
    }

    @Test
    public void validateArgsNumber_correctArgs_sadPath() {
        final InputValidator validator = InputValidator.builder()
                .expectedNumArgs(4)
                .build();
        final String[] input = {"a", "b", "c"};
        assertThrows(InputValidationException.class,
                () -> validator.validateArgsNumber(input));
    }

    @Test
    public void validateCommandSenderType_happyPath_PlayerOnly() {
        final InputValidator validator = InputValidator.builder()
                .playerOnly()
                .build();
        validator.validateCommandSenderType(player);
    }

    @Test
    public void validateCommandSenderType_sadPath_PlayerOnly() {
        final InputValidator validator = InputValidator.builder()
                .playerOnly()
                .build();
        assertThrows(InputValidationException.class,
                () -> validator.validateCommandSenderType(abstractHorse));
    }

    // TODO: more validate tests
}
