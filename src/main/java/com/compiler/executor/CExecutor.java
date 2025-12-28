package com.compiler.executor;

import java.io.*;
import java.util.concurrent.*;

public class CExecutor implements LanguageExecutor {

    @Override
    public String execute(String code, String input, int timeoutSeconds) throws Exception {

        File dir = new File("temp/c");
        dir.mkdirs();

        File source = new File(dir, "main.c");
        try (FileWriter fw = new FileWriter(source)) {
            fw.write(code);
        }

        Process compile = new ProcessBuilder("gcc", "main.c", "-o", "main")
                .directory(dir)
                .start();

        if (compile.waitFor() != 0) {
            return new String(compile.getErrorStream().readAllBytes());
        }

        Process run = new ProcessBuilder("./main")
                .directory(dir)
                .start();

        if (input != null && !input.isEmpty()) {
            run.getOutputStream().write(input.getBytes());
            run.getOutputStream().close();
        }

        return readWithTimeout(run, timeoutSeconds);
    }

    private String readWithTimeout(Process process, int timeoutSeconds) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() ->
                new String(process.getInputStream().readAllBytes())
        );

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS).trim();
        } catch (TimeoutException e) {
            process.destroyForcibly();
            return "TIME_LIMIT_EXCEEDED";
        } finally {
            executor.shutdown();
        }
    }
}
