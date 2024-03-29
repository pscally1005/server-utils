package com.scally.serverutils.template;

import com.scally.serverutils.slabs.SlabsChangeset;
import com.scally.serverutils.undo.Change;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.ChangesetList;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.Coordinates;
import com.scally.serverutils.validation.InputValidationErrorCode;
import com.scally.serverutils.validation.InputValidationException;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TemplateReplaceCommandExecutorTest {

    private class TestTemplateReplaceCommandExecutor extends TemplateReplaceCommandExecutor {

        private final InputValidator inputValidator;
        private final Changeset changeset;
        private Change change;

        public TestTemplateReplaceCommandExecutor(UndoManager undoManager,
                                                  InputValidator inputValidator,
                                                  Changeset changeset) {
            super(undoManager);
            this.inputValidator = inputValidator;
            this.changeset = changeset;
        }

        @Override
        protected InputValidator inputValidator() {
            return inputValidator;
        }

        @Override
        protected Changeset newChangeset() {
            return changeset;
        }

        @Override
        public Change changeAtLocation(Location location, ValidationResult validationResult) {
            return change;
        }

        @Override
        public List<String> onTabCompleteDistribution(String arg) {
            return null;
        }

        public void stubChangeAtLocation(Change change) {
            this.change = change;
        }
    }

    @Mock
    private UndoManager undoManager;

    @Mock
    private InputValidator inputValidator;

//    @Mock
    private Changeset changeset = new SlabsChangeset();

    @Mock
    private Player commandSender;

    @Mock
    private Command command;

    @Mock
    private Change change;

    @Mock
    private Coordinates coordinates;

    private ValidationResult validationResult;

    private static final String LABEL = "test-template";

    private TestTemplateReplaceCommandExecutor testExecutor;

    @BeforeEach
    public void before() {
        testExecutor = new TestTemplateReplaceCommandExecutor(undoManager, inputValidator, changeset);
    }

    @Test
    public void onCommand_invalidInput_returnsFalse() {
        validationResult = new ValidationResult(false, null, null, null);
        Mockito.when(inputValidator.validate(Mockito.any(), Mockito.any()))
                .thenThrow(new InputValidationException(InputValidationErrorCode.INVALID_COORDINATES));

        final boolean result = testExecutor.onCommand(commandSender, command, LABEL, new String[]{});

        assertFalse(result);
        assert(changeset.count() == 0);
        Mockito.verify(undoManager, Mockito.never()).store(Mockito.any(), Mockito.any());
    }

    @Test
    public void onCommand_validInputNoChange() {
        validationResult = new ValidationResult(true, coordinates, null, null);
        Mockito.when(inputValidator.validate(Mockito.any(), Mockito.any()))
                .thenReturn(validationResult);
        final boolean result = testExecutor.onCommand(commandSender, command, LABEL, new String[]{});
        assertTrue(result);

        assert(changeset.count() == 0);
        Mockito.verify(undoManager, Mockito.never()).store(Mockito.any(), Mockito.any());
    }

    @Test
    public void onCommand_validInputWithChange() {
        validationResult = new ValidationResult(true, coordinates, null, null);
        Mockito.when(inputValidator.validate(Mockito.any(), Mockito.any()))
                .thenReturn(validationResult);

        testExecutor.stubChangeAtLocation(change);
        final boolean result = testExecutor.onCommand(commandSender, command, LABEL, new String[]{});
        assertTrue(result);

        assert(changeset.count() == 1);
        Mockito.verify(undoManager, Mockito.atLeastOnce()).store(Mockito.any(), Mockito.any());
    }

}
