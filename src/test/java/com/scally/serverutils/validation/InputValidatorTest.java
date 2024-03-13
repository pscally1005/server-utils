package com.scally.serverutils.validation;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Tag;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InputValidatorTest {

    private InputValidator inputValidator;

    @Mock
    private Player player;

    @Mock
    private BlockCommandSender blockCommandSender;

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
    void validate_happyPath2() {
        final String[] args = validArgs();

        final ValidationResult result = inputValidator.validate(blockCommandSender, args);

        assertTrue(result.validated());
        assertNotNull(result.coordinates());
        assertNotNull(result.fromDistribution());
        assertNotNull(result.toDistribution());
    }

    @Test
    void validate_invalidNumberOfArgs() {
        final String[] args = new String[] { "0", "0", "0" };
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(player, args));
        assertEquals(InputValidationErrorCode.INVALID_ARGS_NUMBER, exception.getErrorCode());
    }

    @Test
    void validate_invalidCommandSenderType() {
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(abstractHorse, validArgs()));
        assertEquals(InputValidationErrorCode.COMMAND_SENDER_NOT_PLAYER, exception.getErrorCode());
    }


    @Test
    void validate_invalidFromDistribution() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "oak_slab_2_electric_boogaloo",
                "birch_slab,jungle_slab"
        };
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(player, args));
        assertEquals(InputValidationErrorCode.INVALID_DISTRIBUTION_TYPES, exception.getErrorCode());
    }

    @Test
    void validate_invalidToDistribution() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "5", "5",
                "birch_slab,jungle_slab",
                "oak_slab_2_electric_boogaloo"
                //lol nice
        };
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(player, args));
        assertEquals(InputValidationErrorCode.INVALID_DISTRIBUTION_TYPES, exception.getErrorCode());
    }

    @Test
    void validate_invalidCoordinates() {
        final String[] args = new String[] {
                "0", "0", "0",
                "5", "y", "5",
                "oak_slab",
                "birch_slab,jungle_slab"
        };
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(player, args));
        assertEquals(InputValidationErrorCode.INVALID_COORDINATES, exception.getErrorCode());
    }

    @Test
    void validate_invalidVolumeSize() {
        final String[] args = new String[] {
                "-1000000", "-1000000", "-1000000",
                "1000000", "1000000", "1000000",
                "oak_slab",
                "birch_slab,jungle_slab"
        };
        final InputValidationException exception = assertThrowsExactly(InputValidationException.class,
                () -> inputValidator.validate(player, args));
        assertEquals(InputValidationErrorCode.VOLUME_TOO_LARGE, exception.getErrorCode());
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
