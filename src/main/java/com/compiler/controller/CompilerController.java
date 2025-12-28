package com.compiler.controller;

import com.compiler.executor.*;
import com.compiler.service.TestCaseService;
import com.compiler.dto.CodeExecutionRequest;
import com.compiler.dto.CodeExecutionResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin
public class CompilerController {

    private final TestCaseService testCaseService;

    private final JavaExecutor javaExecutor;
    private final CExecutor cExecutor;
    private final CppExecutor cppExecutor;
    private final PythonExecutor pythonExecutor;
    private final JavaScriptExecutor javaScriptExecutor;

    public CompilerController(
            TestCaseService testCaseService,
            JavaExecutor javaExecutor,
            CExecutor cExecutor,
            CppExecutor cppExecutor,
            PythonExecutor pythonExecutor,
            JavaScriptExecutor javaScriptExecutor
    ) {
        this.testCaseService = testCaseService;
        this.javaExecutor = javaExecutor;
        this.cExecutor = cExecutor;
        this.cppExecutor = cppExecutor;
        this.pythonExecutor = pythonExecutor;
        this.javaScriptExecutor = javaScriptExecutor;
    }

    @PostMapping("/execute")
    public CodeExecutionResponse execute(@RequestBody CodeExecutionRequest request) throws Exception {

        LanguageExecutor executor;

        switch (request.getLanguage().toLowerCase()) {
            case "java" -> executor = javaExecutor;
            case "c" -> executor = cExecutor;
            case "cpp" -> executor = cppExecutor;
            case "python" -> executor = pythonExecutor;
            case "javascript" -> executor = javaScriptExecutor;
            default -> throw new IllegalArgumentException("Unsupported language");
        }

        return testCaseService.runTests(request, executor);
    }
}
