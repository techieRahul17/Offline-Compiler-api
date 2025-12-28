package com.compiler.service;

import com.compiler.dto.*;
import com.compiler.executor.LanguageExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseService {

    public CodeExecutionResponse runTests(
            CodeExecutionRequest request,
            LanguageExecutor executor
    ) {

        CodeExecutionResponse response = new CodeExecutionResponse();
        List<CodeExecutionResponse.TestResult> results = new ArrayList<>();

        boolean allPassed = true;

        for (int i = 0; i < request.getTestCases().size(); i++) {

            TestCaseDTO tc = request.getTestCases().get(i);
            CodeExecutionResponse.TestResult result =
                    new CodeExecutionResponse.TestResult();

            result.testCaseNumber = i + 1;
            result.expected = normalize(tc.getExpectedOutput());

            try {
                String output = executor.execute(
                        request.getCode(),
                        tc.getInput(),
                        request.getTimeoutSeconds()
                );

                if (output.startsWith("COMPILE_ERROR")) {
    response.setStatus("FAILED");
    response.setErrorType("COMPILE_ERROR");
    response.setMessage(output);
    return response;
}

if (output.startsWith("RUNTIME_ERROR")) {
    response.setStatus("FAILED");
    response.setErrorType("RUNTIME_ERROR");
    response.setMessage(output);
    return response;
}

if ("TIME_LIMIT_EXCEEDED".equals(output)) {
    response.setStatus("FAILED");
    response.setErrorType("TIME_LIMIT_EXCEEDED");
    response.setMessage("Execution timed out");
    return response;
}

                else if (normalize(output).equals(result.expected)) {
                    result.status = "PASSED";
                    result.output = normalize(output);
                }
                else {
                    result.status = "FAILED";
                    result.output = normalize(output);
                    result.error = "Wrong Answer";
                    allPassed = false;
                }

            } catch (Exception e) {
                result.status = "FAILED";
                result.error = "Runtime Error: " + e.getMessage();
                allPassed = false;
            }

            results.add(result);

            if (!allPassed) break; // stop on first failure (JDoodle style)
        }

        response.setResults(results);

        if (allPassed) {
            response.setStatus("SUCCESS");
            response.setMessage("All test cases passed");
        } else {
            response.setStatus("FAILED");
            response.setErrorType("WRONG_ANSWER");
            response.setMessage("One or more test cases failed");
        }

        return response;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }
}
