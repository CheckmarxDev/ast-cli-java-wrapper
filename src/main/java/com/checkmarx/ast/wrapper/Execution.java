package com.checkmarx.ast.wrapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public final class Execution {

    private Execution() {

    }

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";
    private static final List<String> OS_MAC = Arrays.asList("mac os x", "darwin", "osx");
    private static final String FILE_NAME_LINUX = "cx-linux";
    private static final String FILE_NAME_MAC = "cx-mac";
    private static final String FILE_NAME_WINDOWS = "cx.exe";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static String executable = null;

    static <T> T executeCommand(List<String> arguments,
                                Logger logger,
                                Function<String, T> lineParser)
            throws IOException, InterruptedException, CxException {
        Process process = buildProcess(arguments);
        try (BufferedReader br = getReader(process)) {
            T executionResult = null;
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                logger.info(line);
                stringBuilder.append(line).append(LINE_SEPARATOR);
                T parsedLine = lineParser.apply(line);
                if (parsedLine != null) {
                    executionResult = parsedLine;
                }
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new CxException(process.exitValue(), stringBuilder.toString());
            }
            return executionResult;
        }
    }

    static String executeCommand(List<String> arguments,
                                 Logger logger,
                                 String directory,
                                 String file)
            throws IOException, InterruptedException, CxException {
        Process process = buildProcess(arguments);

        try (BufferedReader br = getReader(process)) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                logger.info(line);
                stringBuilder.append(line).append(LINE_SEPARATOR);
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new CxException(process.exitValue(), stringBuilder.toString());
            }
        }

        File outputFile = new File(directory, file);

        return new String(Files.readAllBytes(Paths.get(outputFile.getAbsolutePath())),
                          StandardCharsets.UTF_8);
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

    public static String getTempBinary() throws IOException {
        if (executable == null) {
            String fileName = detectBinaryName();
            if (fileName == null) {
                throw new IOException("Unsupported architecture");
            }
            URL resource = Execution.class.getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new NoSuchFileException("Could not find CLI executable");
            }
            File tempExecutable = new File(TEMP_DIR, fileName);
            if (!tempExecutable.exists() || !compareChecksum(resource.openStream(),
                                                             new FileInputStream(tempExecutable))) {
                FileUtils.copyURLToFile(resource, tempExecutable);
            }
            if (!tempExecutable.canExecute() && !tempExecutable.setExecutable(true)) {
                throw new IOException("Could not set CLI as executable");
            }
            executable = tempExecutable.getAbsolutePath();
        }
        return executable;
    }

    private static String detectBinaryName() {
        String arch = OS_NAME;
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
        return fileName;
    }

    private static boolean compareChecksum(InputStream a, InputStream b) throws IOException {
        return Objects.equals(DigestUtils.md5Hex(a), DigestUtils.md5Hex(b));
    }
}
