package com.compiler.dto;

import java.util.List;

public class CodeExecutionResponse {

    private String status;        // SUCCESS / FAILED
    private String errorType;     // COMPILE_ERROR, RUNTIME_ERROR, etc.
    private String message;       // Human-readable message
    private List<TestResult> results;

    public static class TestResult {
        public int testCaseNumber;
        public String status;     // PASSED / FAILED
        public String output;
        public String expected;
        public String error;      // per-test error message
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<TestResult> getResults() { return results; }
    public void setResults(List<TestResult> results) { this.results = results; }
}
