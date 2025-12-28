package com.compiler.controller;

import com.compiler.dto.CodeExecutionRequest;
import com.compiler.dto.CodeExecutionResponse;
import com.compiler.executor.*;
import com.compiler.service.TestCaseService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin
public class CompilerController {

    private final TestCaseService testCaseService;

    public CompilerController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/execute")
    public CodeExecutionResponse execute(
            @RequestBody CodeExecutionRequest request
    ) throws Exception {

        LanguageExecutor executor;

        switch (request.getLanguage().toLowerCase()) {
            case "java":
                executor = new JavaExecutor();
                break;
            case "c":
                executor = new CExecutor();
                break;
            case "cpp":
                executor = new CppExecutor();
                break;
            case "python":
                executor = new PythonExecutor();
                break;
            case "javascript":
                executor = new JavaScriptExecutor();
                break;
            default:
                throw new RuntimeException("Unsupported language");
        }

        return testCaseService.runTests(request, executor);
    }
}
