package com.checkmarx.ast.wrapper;

import org.slf4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

final class Execution {

    private Execution() {

    }

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";
    private static final List<String> OS_MAC = Arrays.asList("mac os x", "darwin", "osx");
    private static final String FILE_NAME_LINUX = "cx-linux";
    private static final String FILE_NAME_MAC = "cx-mac";
    private static final String FILE_NAME_WINDOWS = "cx.exe";
    private static final String UNSUPPORTED_ARCH = "Unsupported architecture";

    static <T> CxOutput<T> executeCommand(List<String> arguments,
                                          Logger logger,
                                          Function<String, T> lineParser)
            throws IOException, InterruptedException {
        Process process = buildProcess(arguments);
        try (BufferedReader br = getReader(process)) {
            T executionResult = null;
            String line;
            while ((line = br.readLine()) != null) {
                logger.info(line);
                T parsedLine = lineParser.apply(line);
                if (parsedLine != null) {
                    executionResult = parsedLine;
                }
            }
            process.waitFor();
            return new CxOutput<>(process.exitValue(), executionResult);
        }
    }

    static CxOutput<String> executeCommand(List<String> arguments,
                                           Logger logger,
                                           String directory,
                                           String file)
            throws IOException, InterruptedException {
        Process process = buildProcess(arguments);
        process.waitFor();
        if (process.exitValue() != 0) {
            return new CxOutput<>(process.exitValue(), null);
        }

        try (BufferedReader br = getReader(process)) {
            String line;
            while ((line = br.readLine()) != null) {
                logger.info(line);
            }
        }

        File outputFile = new File(directory, file);
        String fileContent = new String(Files.readAllBytes(Paths.get(outputFile.getAbsolutePath())),
                                        StandardCharsets.UTF_8);

        return new CxOutput<>(process.exitValue(), fileContent);
    }

    private static BufferedReader getReader(Process process) {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }

    private static Process buildProcess(List<String> commands) throws IOException {
        ProcessBuilder lmBuilder = new ProcessBuilder(commands);
        lmBuilder.redirectErrorStream(true);
        return lmBuilder.start();
    }

    /**
     * Detect binary name by the current architecture.
     *
     * @return binary name
     * @throws IOException when architecture is unsupported
     */
    static URI detectBinary() throws IOException, URISyntaxException {
        final String arch = OS_NAME;
        String fileName = null;
        if (arch.contains(OS_LINUX)) {
            fileName = FILE_NAME_LINUX;
        } else if (arch.contains(OS_WINDOWS)) {
            fileName = FILE_NAME_WINDOWS;
        } else {
            for (String macStr : OS_MAC) {
                if (arch.contains(macStr)) {
                    fileName = FILE_NAME_MAC;
                    break;
                }
            }
        }
        if (fileName == null) {
            throw new IOException(UNSUPPORTED_ARCH);
        }
        URL resource = Execution.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new NoSuchFileException("could not find CLI executable");
        }
        return resource.toURI();
    }
}
