import com.scally.serverutils.executors.SlabsCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SlabsCommandExecutorTest {

    @Mock
    private CommandSender commandSender;

    @Mock
    private Command command;

    private AutoCloseable mocks;

    private final SlabsCommandExecutor slabsCommandExecutor = new SlabsCommandExecutor();

    @BeforeEach
    public void before() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void after() throws Exception {
        mocks.close();
    }

    @Test
    public void slabsCommand_invalidNumberOfArgs() {
        final String[] args = new String[2];
        args[0] = "oak_slab";
        args[1] = "birch_slab";

        final boolean result = slabsCommandExecutor.onCommand(commandSender, command, "slabs", args);
        Assertions.assertFalse(result);
    }

}
