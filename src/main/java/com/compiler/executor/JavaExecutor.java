package com.compiler.executor;

import java.io.*;
import java.util.concurrent.*;

public class JavaExecutor implements LanguageExecutor {

   @Override
public String execute(String code, String input, int timeoutSeconds) throws Exception {

    File dir = new File("temp/java");
    dir.mkdirs();

    File source = new File(dir, "Main.java");
    try (FileWriter fw = new FileWriter(source)) {
        fw.write(code);
    }

    // ---------- COMPILE ----------
    Process compile = new ProcessBuilder("javac", "Main.java")
            .directory(dir)
            .start();

    compile.waitFor();

    if (compile.exitValue() != 0) {
        return "COMPILE_ERROR:\n" +
                new String(compile.getErrorStream().readAllBytes());
    }

    // ---------- RUN ----------
    Process run = new ProcessBuilder("java", "Main")
            .directory(dir)
            .start();

    if (input != null && !input.isEmpty()) {
        run.getOutputStream().write(input.getBytes());
        run.getOutputStream().close();
    }

    boolean finished = run.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

    if (!finished) {
        run.destroyForcibly();
        return "TIME_LIMIT_EXCEEDED";
    }

    // 🔴 CRITICAL PART
    if (run.exitValue() != 0) {
        return "RUNTIME_ERROR:\n" +
                new String(run.getErrorStream().readAllBytes());
    }

    return new String(run.getInputStream().readAllBytes()).trim();
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
