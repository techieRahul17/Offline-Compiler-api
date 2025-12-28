# Offline Compiler API 

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Repo Size](https://img.shields.io/github/repo-size/techieRahul17/Offline-Compiler-api)](https://github.com/techieRahul17/Offline-Compiler-api)
[![Last Commit](https://img.shields.io/github/last-commit/techieRahul17/Offline-Compiler-api)](https://github.com/techieRahul17/Offline-Compiler-api/commits/main)

A offline, multi-language compiler & judge API built with Spring Boot.  
This project compiles, runs, and judges submitted code locally with support for multiple languages, test-case evaluation, and clear error classification — useful for coding platforms, interview systems, educational labs, and offline judge environments.

---

## Table of Contents

- [Why Offline Compiler API?](#why-offline-compiler-api)
- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [API Reference](#api-reference)
  - [Endpoint](#endpoint)
  - [Request Body](#request-body)
  - [Responses](#responses)
- [Error Types](#error-types)
- [System Requirements](#system-requirements)
- [Running Locally](#running-locally)
- [Docker (recommended for sandboxing)](#docker-recommended-for-sandboxing)
- [Security Notes](#security-notes)
- [Contributing](#contributing)
- [Testing](#testing)
- [License](#license)
- [Author](#author)
- [Support](#support)

---

## Why Offline Compiler API?

Many compiler APIs are cloud-hosted. This project focuses on:

- Offline execution (no cloud required)  
- Full control over compilation & runtime environment  
- Test-case-based judging suitable for coding platforms  
- Clear and actionable error types for better UX and debugging  
- Easily extensible to new languages or sandbox techniques

---

## Features

- Multi-language support:
  - Java
  - C
  - C++
  - Python
  - JavaScript (Node)
- Test-case based evaluation with detailed per-test feedback
- Error handling and classification:
  - SUCCESS, WRONG_ANSWER, COMPILE_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED
- Execution timeouts & resource safeguards (configurable)
- Clean REST API (JSON in / JSON out)
- Offline-first — run entirely on local or self-hosted infra
- Extensible LanguageExecutor design for new languages
- Developer-friendly and ready for integration with UIs or services

---

## Architecture Overview

High-level flow:

```
Controller
↓
TestCaseService
↓
LanguageExecutor (Java / C / C++ / Python / JS)
↓
Compiler & Runtime (Local Machine)
↓
Judge & Response Formatter
```

Each LanguageExecutor is responsible for:
- persisting code to disk
- compiling (if applicable)
- running with timeouts & capturing stdout/stderr
- returning execution results to the judge

---

##  API Reference

### Endpoint

```
POST /api/compiler/execute
```

Content-Type: `application/json`

### Request Body

Example:

```json
{
  "language": "java",
  "code": "public class Main { public static void main(String[] args){ System.out.println(10); }}",
  "timeoutSeconds": 2,
  "testCases": [
    {
      "input": "",
      "expectedOutput": "10"
    }
  ]
}
```

Fields:
- language: one of `java`, `c`, `cpp`, `python`, `javascript` (or the identifier your implementation expects)
- code: source code string
- timeoutSeconds: maximum allowed execution seconds per test case
- testCases: array of { input: string, expectedOutput: string } — judge compares normalized outputs (trim newline differences)

---

### Successful Response (All Tests Passed)

```json
{
  "status": "SUCCESS",
  "results": [
    {
      "testCaseNumber": 1,
      "status": "PASSED",
      "output": "10",
      "expected": "10",
      "elapsedMillis": 45
    }
  ],
  "summary": {
    "passed": 1,
    "failed": 0,
    "total": 1
  }
}
```

---

### Compile Error Response

```json
{
  "status": "FAILED",
  "errorType": "COMPILE_ERROR",
  "message": "Main.java: error: ';' expected\n 1 error"
}
```

---

### Runtime Error Response

```json
{
  "status": "FAILED",
  "errorType": "RUNTIME_ERROR",
  "message": "java.lang.ArithmeticException: / by zero",
  "stderr": "Exception in thread \"main\" java.lang.ArithmeticException: / by zero\n\tat Main.main(Main.java:3)"
}
```

---

### Time Limit Exceeded Response

```json
{
  "status": "FAILED",
  "errorType": "TIME_LIMIT_EXCEEDED",
  "message": "Execution exceeded 2 seconds timeout"
}
```

---

### Wrong Answer Response

```json
{
  "status": "FAILED",
  "errorType": "WRONG_ANSWER",
  "results": [
    {
      "testCaseNumber": 1,
      "status": "FAILED",
      "output": "8",
      "expected": "10",
      "error": "Wrong Answer"
    }
  ],
  "summary": {
    "passed": 0,
    "failed": 1,
    "total": 1
  }
}
```

---

##  Supported Error Types

| Error Type          | Description                                    |
| ------------------- | ---------------------------------------------- |
| SUCCESS             | All test cases passed                          |
| WRONG_ANSWER        | Output did not match expected output           |
| COMPILE_ERROR       | Compilation failed (languages that compile)    |
| RUNTIME_ERROR       | Program crashed at runtime                     |
| TIME_LIMIT_EXCEEDED | Execution exceeded configured timeout          |

---

##  System Requirements

Install these on the host where you run the server:

```bash
java --version
gcc --version
g++ --version
python --version
node --version
mvn --version
```

Notes:
- Java (JDK 11+) for the Spring Boot app.
- Compilers/interpreters must be callable from PATH for LanguageExecutors.
- For production/public-facing use, run inside a secure sandbox (Docker/container, seccomp, cgroups).

---

##  Running the Project Locally

Build & run with Maven:

```bash
mvn clean package
mvn -DskipTests=false spring-boot:run
```

Or run the generated JAR:

```bash
java -jar target/offline-compiler-api-<version>.jar
```

By default the server runs at:

```
http://localhost:8080
```

You can change the port or other properties in `application.properties` or via environment variables.

---

## Docker (recommended for sandboxing)

A recommended approach for safer execution is to run code execution inside Docker containers. Example (high level):

1. Build the Spring Boot app image.
2. Ensure the executor service runs code inside restricted containers.
3. Use resource limits (CPU, memory) and timeouts.

(If you would like, a Dockerfile + docker-compose example can be added to this repo.)

---

##  Security Notes

- Current design executes on the host environment. Use only in trusted or controlled environments.
- Timeouts are enforced but additional measures are recommended for production:
  - Run code inside containers (Docker)
  - Use seccomp or other kernel-level restriction
  - Limit memory and CPU for execution jobs
  - Validate and sanitize inputs where applicable

---

##  Contributing

Contributions are welcome — whether it is new language support, better sandboxing, improved test reporting, or documentation.

Suggested workflow:
1. Fork the repo
2. Create a branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -m "Add some feature"`
4. Push to your fork: `git push origin feature/my-feature`
5. Open a Pull Request

Please follow standard guidelines: meaningful commits, tests for new behavior, and a clear PR description. Consider adding CHANGELOG entries for breaking changes.

See `CONTRIBUTING.md` (if present) for more details.

---

##  Testing

- Unit tests: use `mvn test`
- Integration tests: add tests to validate compilation & runtime flows (consider using Docker in CI)
- When adding languages, include sample positive and failing test-cases

---

##  License

This project is licensed under the MIT License.

```
MIT License © 2025 Rahul V S
```

---

##  Author


**Rahul V S**  
GitHub: [Rahul V S](https://github.com/techieRahul17)


---
## Technologies & Licenses

![Java](https://img.shields.io/badge/Java-17+-blue)
![Docker](https://img.shields.io/badge/Docker-Sandboxed-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Stable-brightgreen)

---

##  Support

If you find this project useful:

- Star the repository
- Report issues
- Submit improvements via PRs

Let’s build better developer tools together 

Happy Contributing!!!
