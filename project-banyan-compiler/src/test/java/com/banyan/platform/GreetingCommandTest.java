package com.banyan.platform;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;

@Disabled("Quarkus TopCommand bean not registered in test scope")
@QuarkusMainTest
public class GreetingCommandTest {

    @Test
    public void testBasicLaunch(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch();
        assertEquals(result.exitCode(), 0);
    }

    @Test
    @Launch({ "Alice" })
    public void testLaunchWithArguments(LaunchResult result) {
        assertEquals(0, result.exitCode());
    }

}
