package com.scally.serverutils.validation;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InputValidatorTest {

    private InputValidator inputValidator;

    @Mock
    private Player player;

    @Mock
    private AbstractHorse abstractHorse;

    @BeforeEach
    void before() {
        MockBukkit.mock();
        inputValidator = InputValidator.builder()
                .expectedNumArgs(8)
                .playerOnly()
                .withCoordinateValidation()
                .withFromDistribution(6, Tag.SLABS)
                .withToDistribution(7, Tag.SLABS)
                .build();
    }

    @AfterEach
    void after() {
        MockBukkit.unmock();
    }

    @Test
    void validate_happyPath() {
        final String[] args = validArgs();

        final ValidationResult result = inputValidator.validate(player, args);

        assertTrue(result.validated());
        assertNotNull(result.coordinates());
        assertNotNull(result.fromDistribution());
        assertNotNull(result.toDistribution());
    }

    @Test
    void validate_invalidNumberOfArgs() {
        final String[] args = new String[] { "0", "0", "0" };
        final ValidationResult result = inputValidator.validate(player, args);
        assertInvalid(result);
    }

    @Test
    void validate_invalidCommandSenderType() {
        final ValidationResult result = inputValidator.validate(abstractHorse, validArgs());
        assertInvalid(result);
    }

    @Test
    void validate_invalidFromDistribution() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "oak_slab_2_electric_boogaloo",
                "birch_slab,jungle_slab"
        };
        final ValidationResult result = inputValidator.validate(player, args);
        assertInvalid(result);
    }

    @Test
    void validate_invalidToDistribution() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "birch_slab,jungle_slab",
                "oak_slab_2_electric_boogaloo"
        };
        final ValidationResult result = inputValidator.validate(player, args);
        assertInvalid(result);
    }

    @Test
    void validate_invalidCoordinates() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "y", "5",
                "oak_slab",
                "birch_slab,jungle_slab"
        };
        final ValidationResult result = inputValidator.validate(player, args);
        assertInvalid(result);
    }

    @Test
    void validate_invalidVolumeSize() {
        final String[] args = new String[] {
                "-1000000", "-1000000", "-1000000",
                "1000000", "1000000", "1000000",
                "oak_slab",
                "birch_slab,jungle_slab"
        };
        final ValidationResult result = inputValidator.validate(player, args);
        assertInvalid(result);
    }

    private void assertInvalid(ValidationResult result) {
        assertFalse(result.validated());
        assertNull(result.coordinates());
        assertNull(result.fromDistribution());
        assertNull(result.toDistribution());
    }

    private String[] validArgs() {
        return new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "oak_slab",
                "birch_slab,jungle_slab"
        };
    }
}
