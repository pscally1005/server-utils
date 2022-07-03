package com.scally.serverutils.chat;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatMessageSenderTest {

    @Mock
    private CommandSender commandSender;

    private AutoCloseable mocks;

    private ChatMessageSender messageSender = new ChatMessageSender();

    @BeforeEach
    public void before() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void after() throws Exception {
        mocks.close();
    }

    @Test
    public void sendSuccess_worksProperly() {
        messageSender.sendSuccess(commandSender, "Test");
        Mockito.verify(commandSender, Mockito.times(1))
                .sendMessage("§aTest");
    }

    @Test
    public void sendError_worksProperly() {
        messageSender.sendError(commandSender, "Test");
        Mockito.verify(commandSender, Mockito.times(1))
                .sendMessage("§cTest");
    }

}
