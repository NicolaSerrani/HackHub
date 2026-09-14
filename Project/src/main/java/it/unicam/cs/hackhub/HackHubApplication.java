package it.unicam.cs.hackhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class HackHubApplication {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        stopPreviousInstance();
        SpringApplication.run(HackHubApplication.class, args);
    }

    private static void stopPreviousInstance() {
        if (!System.getProperty("os.name").startsWith("Windows")) return;
        try {
            String output = new String(new ProcessBuilder("cmd", "/c", "netstat -ano -p tcp")
                    .start().getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            output.lines().map(HackHubApplication::listenerPid).flatMap(Optional::stream)
                    .map(ProcessHandle::of).flatMap(Optional::stream)
                    .filter(process -> process.info().user().equals(ProcessHandle.current().info().user()))
                    .filter(process -> process.info().command().map(command -> command.toLowerCase().endsWith("java.exe"))
                            .orElse(false))
                    .forEach(process -> {
                        process.destroy();
                        try {
                            process.onExit().get(5, TimeUnit.SECONDS);
                        } catch (TimeoutException exception) {
                            process.destroyForcibly();
                        } catch (Exception exception) {
                            throw new IllegalStateException("Unable to stop the previous HackHub instance", exception);
                        }
                    });
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to check port " + PORT, exception);
        }
    }

    static Optional<Long> listenerPid(String line) {
        String[] columns = line.trim().split("\\s+");
        if (columns.length != 5 || !columns[1].endsWith(":" + PORT) || !columns[3].equals("LISTENING")) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(columns[4]));
    }
}
