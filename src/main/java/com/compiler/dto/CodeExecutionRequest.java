package com.compiler.dto;

import java.util.List;

public class CodeExecutionRequest {

    private String language;
    private String code;
    private List<TestCaseDTO> testCases;
    private int timeoutSeconds;

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public List<TestCaseDTO> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseDTO> testCases) { this.testCases = testCases; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
}
