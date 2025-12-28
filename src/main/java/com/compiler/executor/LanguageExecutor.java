package com.compiler.executor;

public interface LanguageExecutor {

    String execute(String code, String input, int timeoutSeconds) throws Exception;
}
