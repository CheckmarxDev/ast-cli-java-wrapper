package com.checkmarx.ast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class Execution {

    private static final Logger LOGGER = LoggerFactory.getLogger(Execution.class);

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

    public static Process executeCommand(List<String> commands) throws IOException {
        ProcessBuilder lmBuilder = new ProcessBuilder(commands);
        lmBuilder.redirectErrorStream(true);
        return lmBuilder.start();
    }

    static <T> CLIOutput<T> executeCommand(List<String> commands, Function<String, T> lineParser)
            throws IOException, InterruptedException {
        Process process = buildProcess(commands);
        try (BufferedReader br = getReader(process)) {
            T executionResult = null;
            String line;
            while ((line = br.readLine()) != null) {
                LOGGER.debug(line);
                if (!StringUtils.isBlank(line) && isValidJSON(line)) {
                    executionResult = lineParser.apply(line);
                }
            }
            process.waitFor();
            return new CLIOutput<>(process.exitValue(), executionResult);
        }
    }

    static CLIOutput<String> executeTextCommand(List<String> commands)
            throws IOException, InterruptedException {
        Process process = buildProcess(commands);
        try (BufferedReader br = getReader(process)) {
            process.waitFor();
            String line = br.readLine();
            LOGGER.debug(line);
            return new CLIOutput<>(process.exitValue(), line);
        }
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

    private static boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().createParser(json);
            //noinspection StatementWithEmptyBody
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (IOException ignored) {
        }
        return valid;
    }


    /**
     * Detect binary name by the current architecture.
     *
     * @return binary name
     * @throws IOException when architecture is unsupported
     */
    public static String detectBinary() throws IOException {
        final String arch = OS_NAME;
        if (arch.contains(OS_LINUX)) {
            return FILE_NAME_LINUX;
        } else if (arch.contains(OS_WINDOWS)) {
            return FILE_NAME_WINDOWS;
        } else {
            for (String macStr : OS_MAC) {
                if (arch.contains(macStr)) {
                    return FILE_NAME_MAC;
                }
            }
        }
        throw new IOException(UNSUPPORTED_ARCH);
    }
}
