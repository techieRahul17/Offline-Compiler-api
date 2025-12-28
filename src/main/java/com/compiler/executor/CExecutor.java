package com.compiler.executor;

import org.springframework.stereotype.Component;

import com.compiler.service.DockerSandboxService;

@Component
public class CExecutor implements LanguageExecutor {

    private final DockerSandboxService sandbox;

    public CExecutor(DockerSandboxService sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String execute(String code, String input, int timeoutSeconds) throws Exception {

        var r = sandbox.execute(
                "gcc main.c -o main && ./main",
                "main.c",
                code,
                timeoutSeconds
        );

        return classify(r);
    }

    private String classify(DockerSandboxService.ExecutionResult r) {
        if (r.exitCode() == 124) return "TIME_LIMIT_EXCEEDED";
        if (r.exitCode() != 0 && r.stderr().contains("error"))
            return "COMPILE_ERROR:\n" + r.stderr();
        if (r.exitCode() != 0)
            return "RUNTIME_ERROR:\n" + r.stderr();
        return r.stdout();
    }
}
