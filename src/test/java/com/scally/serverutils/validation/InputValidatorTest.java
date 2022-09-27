package com.scally.serverutils.validation;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputValidatorTest {

    private ServerMock server;

    @BeforeEach
    public void before() {
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
                .withFromDistribution(6, BlockData.class)
                .withToDistribution(7, BlockData.class)
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

    // TODO: more validate tests
}
