package com.checkmarx.ast.scans;

import com.checkmarx.ast.exceptions.CxException;
import com.checkmarx.ast.executionservice.ExecutionService;
import com.checkmarx.ast.results.CxCommandOutput;
import com.checkmarx.ast.results.structure.CxResultOutput;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CxAuth {
    private Logger log = LoggerFactory.getLogger(CxAuth.class.getName());
    private final String baseuri;
    private final String baseAuthUri;
    private final String tenant;
    private final String key;
    private final String secret;
    private final String apikey;
    private final List<String> additionalParameters = new ArrayList<>();
    private final URI executable;

    public CxAuth(CxScanConfig scanConfig, Logger log) throws IOException, URISyntaxException, CxException {
        if (scanConfig == null) {
            throw new CxException("CxScanConfig object returned as null!");
        }

        this.baseuri = scanConfig.getBaseUri();
        this.baseAuthUri = scanConfig.getBaseAuthUri();
        this.tenant = scanConfig.getTenant();
        this.key = scanConfig.getClientId();
        this.secret = scanConfig.getClientSecret();
        this.apikey = scanConfig.getApiKey();
        addIndividualParams(this.additionalParameters,
                            Optional.ofNullable(scanConfig.getAdditionalParameters()).orElse(""));

        validateConfigValues();

        if (scanConfig.getPathToExecutable() != null && !scanConfig.getPathToExecutable().isEmpty()) {
            File file = new File(scanConfig.getPathToExecutable());
            this.executable = file.toURI();
        } else {
            this.executable = packageExecutable();
        }

        if (log != null) {
            this.log = log;
        }
    }

    private void validateConfigValues() {
        if (StringUtils.isEmpty(this.baseuri)) {
            throw new CxException("Checkmarx server URL was not set");
        }

        if (StringUtils.isEmpty(this.apikey) && (StringUtils.isEmpty(this.key) && StringUtils.isEmpty(this.secret))) {
            throw new CxException("Credentials were not set");
        }
    }

    private URI packageExecutable() throws IOException, URISyntaxException {
        String osName = System.getProperty("os.name");

        URI uri = getJarURI();
        URI executablePath;
        if (osName.toLowerCase().contains("windows")) {
            executablePath = getFile(uri, "cx.exe");
        } else if (osName.toLowerCase().contains("mac")) {
            executablePath = getFile(uri, "cx-mac");
        } else {
            executablePath = getFile(uri, "cx-linux");
        }
        return executablePath;

    }

    private URI getJarURI() throws URISyntaxException {
        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI uri;

        domain = CxAuth.class.getProtectionDomain();
        source = domain.getCodeSource();
        url = source.getLocation();
        uri = url.toURI();

        return (uri);
    }

    private URI getFile(URI jarLocation, final String fileName) throws IOException {
        final File location;
        final URI fileURI;
        location = new File(jarLocation);

        if (location.isDirectory()) {
            fileURI = URI.create(jarLocation + fileName);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
                log.info("Location of the jar file: {}", fileURI);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        tempFile = File.createTempFile(fileName, " ");
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            close(zipStream);
            close(fileStream);
        }

        return (tempFile.toURI());

    }

    private static void close(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public CxCommandOutput cxScanShow(String id) throws IOException, InterruptedException {
        log.info("Initialized scan retrieval for id: {}", id);
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("show");
        commands.add("--scan-id");
        commands.add(id);
        CxCommandOutput scanObject = runExecutionCommands(commands);
        if (scanObject.getScanObjectList() != null && scanObject.getScanObjectList().size() == 1) {
            log.info("Scan retrieved");
        } else {
            log.info("Did not receive the scan");
        }

        return scanObject;
    }

    public String cxGetResultsSummary(String scanID) throws IOException {
        return runResultExecutionCommands(scanID, "summaryHTML", ".html");
    }

    public String cxGetResultsList(String scanID) throws IOException {
        return runResultExecutionCommands(scanID, "json", ".json");
    }

    public void cxGetResults(String resultType, String scanID, String fileName, String target)
            throws IOException {

        List<String> commands = buildResultCommand(resultType, scanID, fileName, target);

        runResultExecutionCommands(commands);
    }

    private List<String> buildResultCommand(String resultType, String scanId, String outputName, String outputTarget) {
        if (scanId.isEmpty()) {
            throw new CxException("Please provide the scan id ");
        }

        List<String> commands = initialCommandsCommon();
        commands.add("result");
        commands.add("--scan-id");
        commands.add(scanId);
        commands.add("--report-format");
        commands.add(resultType);

        if (StringUtils.isNotBlank(outputName)) {
            commands.add("--output-name");
            commands.add(outputName);
        }

        if (StringUtils.isNotBlank(outputTarget)) {
            commands.add("--output-path");
            commands.add(outputTarget);
        }

        return commands;
    }


    private String runResultExecutionCommands(String scanId, String resultType, String extension) throws IOException {
        Path tempDir = Files.createTempDirectory("cx");
        String fileName = Long.toString(System.nanoTime());
        List<String> commands = buildResultCommand(resultType, scanId, fileName, tempDir.toAbsolutePath().toString());
        runResultExecutionCommands(commands);

        File outputFile = new File(tempDir.toAbsolutePath().toString(), fileName + extension);
        return new String(Files.readAllBytes(Paths.get(outputFile.getAbsolutePath())), StandardCharsets.UTF_8);
    }

    public CxResultOutput cxGetResults(String scanId) throws IOException {
        String results = cxGetResultsList(scanId);
        return new ObjectMapper()
                .readerFor(CxResultOutput.class)
                .readValue(results);
    }

    private String runResultExecutionCommands(List<String> commands) throws IOException {
        log.info("Process submitting to the executor");
        ExecutionService exec = new ExecutionService();
        Process process = exec.executeCommand(commands);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        if (!process.isAlive() && process.exitValue() != 0) {
            log.info("Exit code from CLI is: {} ", process.exitValue());
            return "";
        }
        return builder.toString();
    }

    private CxCommandOutput runExecutionCommands(List<String> commands) throws IOException, InterruptedException {
        log.info("Process submitting to the executor");
        ExecutionService exec = new ExecutionService();
        Process process = exec.executeCommand(commands);
        String line;
        CxScan scanObject;
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        CxCommandOutput cxCommandOutput = new CxCommandOutput();
        while ((line = br.readLine()) != null) {
            log.info(line);
            if (!StringUtils.isBlank(line) && isValidJSON(line)) {
                scanObject = transformToCxScanObject(line);
                List<CxScan> scanList = new ArrayList<>();
                scanList.add(scanObject);
                cxCommandOutput.setScanObjectList(scanList);
            }
        }
        br.close();
        process.waitFor();
        if (!process.isAlive()) {
            cxCommandOutput.setExitCode(process.exitValue());
            log.info("Exit code from AST-CLI: {}", process.exitValue());
        }
        log.info("Process returned from the executor");
        process.destroy();
        return cxCommandOutput;
    }

    private CxScan transformToCxScanObject(String line) {
        ObjectMapper objectMapper = new ObjectMapper();
        CxScan scanObject;
        try {
            scanObject = objectMapper.readValue(line, new TypeReference<CxScan>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
        return scanObject;
    }

    public List<String> initialCommandsCommon() {
        List<String> commands = new ArrayList<>();
        commands.add(executable.getPath());
        addAuthCredentials(commands);

        if (!StringUtils.isEmpty(this.tenant)) {
            commands.add("--tenant");
            commands.add(this.tenant);
        }

        commands.add("--base-uri");
        commands.add(baseuri);

        if (!StringUtils.isEmpty(this.baseAuthUri)) {
            commands.add("--base-auth-uri");
            commands.add(this.baseAuthUri);
        }

        commands.addAll(this.additionalParameters);

        return commands;
    }

    public List<String> initialCommands() {
        List<String> commands = initialCommandsCommon();
        commands.add("--format");
        commands.add("json");

        return commands;
    }

    public Integer cxAuthValidate() throws IOException, InterruptedException {
        log.info("Initialize auth validate command");
        List<String> commands = initialCommandsCommon();
        commands.add("auth");
        commands.add("validate");

        ExecutionService executionService = new ExecutionService();
        return executionService.executeCommandSync(commands);
    }

    public CxCommandOutput cxAstScanList() throws IOException, InterruptedException {
        log.info("Initialized scan list retrieval");
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("list");
        ExecutionService exec = new ExecutionService();
        Process process = exec.executeCommand(commands);
        String line;
        List<CxScan> list = new ArrayList<>();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            if (isValidJSON(line) && !line.isEmpty()) {
                list = transformToCxScanList(line);
            }
        }
        br.close();
        process.waitFor();

        CxCommandOutput cxCommandOutput = new CxCommandOutput();
        cxCommandOutput.setScanObjectList(list);
        cxCommandOutput.setExitCode(process.exitValue());
        if (list != null && !list.isEmpty()) {
            log.info("Retrieved scan list with size: {}", list.size());
        } else {
            log.info("Not able to retrieve scan list");
        }

        return cxCommandOutput;
    }

    public CxCommandOutput cxScanCreate(Map<CxParamType, String> params) throws IOException, InterruptedException {
        log.info("Initialized scan creation");
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("create");

        if (!params.containsKey(CxParamType.PROJECT_NAME)) {
            throw new CxException("Checkmarx project name was not set");
        }

        for (Map.Entry<CxParamType, String> param : params.entrySet()) {
            if (param.getKey() == CxParamType.ADDITIONAL_PARAMETERS && param.getValue() != null) {
                addIndividualParams(commands, param.getValue());
            } else if (param.getKey().toString().length() == 1) {
                commands.add("-" + param.getKey().toString().toLowerCase());
                if (param.getValue() != null) {
                    commands.add(param.getValue());
                } else {
                    commands.add(" ");
                }

            } else if (param.getKey() != CxParamType.ADDITIONAL_PARAMETERS) {
                String paramValue = param.getKey().toString();
                paramValue = "--" + paramValue.replace("_", "-").toLowerCase();
                commands.add(paramValue);
                if (param.getValue() != null) {
                    commands.add(param.getValue());
                } else {
                    commands.add(" ");
                }

            }
        }

        return runExecutionCommands(commands);
    }

    private void addAuthCredentials(List<String> commands) {
        if (key != null && secret != null) {
            commands.add("--client-id");
            commands.add(key);
            commands.add("--client-secret");
            commands.add(secret);
        } else if (apikey != null) {
            commands.add("--apikey");
            commands.add(apikey);
        } else {
            log.info("KEY/SECRET/TOKEN not received");
        }
    }

    private static void addIndividualParams(List<String> commands, String value) {
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(value);
        while (m.find()) {
            commands.add(m.group(1));
        }
    }

    private static List<CxScan> transformToCxScanList(String line) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CxScan> scanList;
        try {
            scanList = objectMapper.readValue(line, new TypeReference<List<CxScan>>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
        return scanList;

    }

    public static boolean isValidJSON(final String json) {
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
}
