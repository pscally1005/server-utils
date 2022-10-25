package com.scally.serverutils.template;

import com.scally.serverutils.undo.Change;
import com.scally.serverutils.undo.Changeset;
import com.scally.serverutils.undo.UndoManager;
import com.scally.serverutils.validation.InputValidator;
import com.scally.serverutils.validation.ValidationResult;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
        protected Changeset changeset() {
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

    @Mock
    private Changeset changeset;

    @Mock
    private CommandSender commandSender;

    @Mock
    private Command command;

    private static final String LABEL = "test-template";

    private TestTemplateReplaceCommandExecutor testExecutor;

    @BeforeEach
    public void before() {
        testExecutor = new TestTemplateReplaceCommandExecutor(undoManager, inputValidator, changeset);
    }

    @Test
    public void onCommand_invalidInput_returnsFalse() {
        Mockito.when(inputValidator.validate(Mockito.any(), Mockito.any()))
                .thenReturn(ValidationResult.invalid());

        final boolean result = testExecutor.onCommand(commandSender, command, LABEL, new String[]{});

        assertFalse(result);
        Mockito.verify(changeset, Mockito.never()).add(Mockito.any());
        Mockito.verify(undoManager, Mockito.never()).store(Mockito.any(), Mockito.any());
    }

}
