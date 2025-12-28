package com.compiler.executor;

import org.springframework.stereotype.Component;

import com.compiler.service.DockerSandboxService;

@Component
public class PythonExecutor implements LanguageExecutor {

    private final DockerSandboxService sandbox;

    public PythonExecutor(DockerSandboxService sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String execute(String code, String input, int timeoutSeconds) throws Exception {

        var r = sandbox.execute(
                "python3 main.py",
                "main.py",
                code,
                timeoutSeconds
        );

        return classify(r);
    }

    private String classify(DockerSandboxService.ExecutionResult r) {
        if (r.exitCode() == 124) return "TIME_LIMIT_EXCEEDED";
        if (r.exitCode() != 0)
            return "RUNTIME_ERROR:\n" + r.stderr();
        return r.stdout();
    }
}
