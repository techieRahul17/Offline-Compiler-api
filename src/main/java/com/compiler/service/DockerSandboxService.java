package com.compiler.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerSandboxService {

    private static final String SANDBOX_IMAGE = "offline-compiler-sandbox";

    public ExecutionResult execute(
            String command,
            String fileName,
            String code,
            int timeoutSeconds
    ) throws Exception {

        Path sandboxDir = Files.createTempDirectory("sandbox-" + UUID.randomUUID());
        Path sourceFile = sandboxDir.resolve(fileName);
        Files.writeString(sourceFile, code);

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "--network", "none",
                "--memory=256m",
                "--cpus=0.5",
                "--pids-limit=64",
                "--read-only",
                "-v", sandboxDir.toAbsolutePath() + ":/sandbox",
                SANDBOX_IMAGE,
                "sh", "-c",
                "cd /sandbox && timeout " + timeoutSeconds + " " + command
        );

        Process process = pb.start();

        boolean finished = process.waitFor(timeoutSeconds + 3, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            cleanup(sandboxDir);
            return new ExecutionResult("", "TIME_LIMIT_EXCEEDED", 124);
        }

        String stdout = new String(process.getInputStream().readAllBytes());
        String stderr = new String(process.getErrorStream().readAllBytes());
        int exitCode = process.exitValue();

        cleanup(sandboxDir);

        return new ExecutionResult(stdout.trim(), stderr.trim(), exitCode);
    }

    private void cleanup(Path dir) throws IOException {
        Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                });
    }

    public record ExecutionResult(String stdout, String stderr, int exitCode) {}
}
