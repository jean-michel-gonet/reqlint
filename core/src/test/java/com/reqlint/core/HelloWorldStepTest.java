package com.reqlint.core;

import com.reqlint.core.testutils.InMemoryLog;
import com.reqlint.core.testutils.LogLevel;
import com.reqlint.core.testutils.LogRecord;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloWorldStepTest {

    @Test
    void helloWorldStepLogsHelloWorld() throws Exception {
        InMemoryLog log = new InMemoryLog();
        Path tempDir = Files.createTempDirectory("otr-test-hello");
        ExecutionContext ctx = new ExecutionContext(tempDir.toString(), false, log);
        HelloWorldStep step = new HelloWorldStep();
        step.execute(ctx);
        assertEquals(List.of(new LogRecord(LogLevel.INFO, "Hello World")), log.getMessages());
    }
}
