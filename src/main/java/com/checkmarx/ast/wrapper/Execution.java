package com.checkmarx.ast.wrapper;

import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private static final String MD5_ALGORITHM = "MD5";

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
                    if (areAllFieldsNotNull(parsedLine) || isAscaRequest(arguments)) {
                        executionResult = parsedLine;
                    }
                }
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new CxException(process.exitValue(), stringBuilder.toString());
            }
            return executionResult;
        }
    }

    public static boolean isAscaRequest(List<String> arguments) {
        return (arguments.size() >= 3 && arguments.get(1).equals("scan") && arguments.get(2).equals("asca"));
    }

    private static boolean areAllFieldsNotNull(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(obj) == null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
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

    static String getTempBinary() throws IOException {
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
                copyURLToFile(resource, tempExecutable);
            }
            if (!tempExecutable.canExecute() && !tempExecutable.setExecutable(true)) {
                throw new IOException("Could not set CLI as executable");
            }
            executable = tempExecutable.getAbsolutePath();
        }
        return executable;
    }

    private static BufferedReader getReader(Process process) {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        return new BufferedReader(isr);
    }

    private static Process buildProcess(List<String> commands) throws IOException {
        ProcessBuilder lmBuilder = new ProcessBuilder(commands);
        lmBuilder.redirectErrorStream(true);
        return lmBuilder.start();
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

    private static void copyURLToFile(URL source, File destination) throws IOException {
        final byte[] buf = new byte[8192];
        try (InputStream reader = source.openStream();
             OutputStream writer = new FileOutputStream(destination)) {
            int i;
            while ((i = reader.read(buf)) != -1) {
                writer.write(buf, 0, i);
            }
        } catch (IOException e) {
            throw new IOException("Could not copy CLI to the temporary directory", e);
        }
    }

    private static boolean compareChecksum(InputStream a, InputStream b) {
        String aMD5 = md5(a);
        String bMD5 = md5(b);
        return aMD5 != null && bMD5 != null && Objects.equals(aMD5, bMD5);
    }

    private static String md5(InputStream a) {
        String md5 = null;
        final byte[] buf = new byte[8192];
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            int i;
            while ((i = a.read(buf)) != -1) {
                md.update(buf, 0, i);
            }
            md5 = new String(md.digest(), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | IOException e) {
            // ignore
        }
        return md5;
    }
}
