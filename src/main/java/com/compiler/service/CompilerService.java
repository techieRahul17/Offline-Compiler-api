package com.compiler.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class CompilerService {

    public String executeJava(String code, String input) throws Exception {

        File dir = new File("temp");
        if (!dir.exists()) dir.mkdir();

        File javaFile = new File(dir, "Main.java");

        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(code);
        }

        // Compile
        Process compileProcess =
                Runtime.getRuntime().exec("javac Main.java", null, dir);

        if (compileProcess.waitFor() != 0) {
            return readStream(compileProcess.getErrorStream());
        }

        // Run
        Process runProcess =
                Runtime.getRuntime().exec("java Main", null, dir);

        if (input != null && !input.isEmpty()) {
            try (BufferedWriter bw =
                         new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                bw.write(input);
                bw.flush();
            }
        }

        return readStream(runProcess.getInputStream());
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
