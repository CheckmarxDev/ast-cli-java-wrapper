package com.checkmarx.ast.scans;

import com.checkmarx.ast.CxException;
import com.checkmarx.ast.Execution;
import com.checkmarx.ast.Scan;
import com.checkmarx.ast.results.CxCommandOutput;
import com.checkmarx.ast.results.CxValidateOutput;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CxAuth {
    private Logger log = LoggerFactory.getLogger(CxAuth.class.getName());
    private final String baseUri;
    private final String baseAuthUri;
    private final String tenant;
    private final String clientId;
    private final String clientSecret;
    private final String apikey;
    private final List<String> additionalParameters = new ArrayList<>();
    private final URI executable;

    public CxAuth(CxScanConfig scanConfig, Logger log) throws IOException, URISyntaxException, CxException {
        Objects.requireNonNull(scanConfig, "configuration object not supplied");

        if (StringUtils.isBlank(scanConfig.getBaseUri())) {
            throw new CxException("checkmarx server URL was not set");
        }

        if (StringUtils.isBlank(scanConfig.getApiKey()) && (StringUtils.isBlank(scanConfig.getClientId())
                                                            && StringUtils.isBlank(scanConfig.getClientSecret()))) {
            throw new CxException("credentials were not set");
        }

        if (log != null) {
            this.log = log;
        }

        this.baseUri = scanConfig.getBaseUri();
        this.baseAuthUri = scanConfig.getBaseAuthUri();
        this.tenant = scanConfig.getTenant();
        this.clientId = scanConfig.getClientId();
        this.clientSecret = scanConfig.getClientSecret();
        this.apikey = scanConfig.getApiKey();
        addIndividualParams(this.additionalParameters,
                            Optional.ofNullable(scanConfig.getAdditionalParameters()).orElse(""));

        if (StringUtils.isBlank(scanConfig.getPathToExecutable())) {
            URL executableURL = getClass().getClassLoader().getResource(Execution.detectBinary());
            Objects.requireNonNull(executableURL, "could not find CLI executable");
            this.executable = executableURL.toURI();
        } else {
            File file = new File(scanConfig.getPathToExecutable());
            this.executable = file.toURI();
        }

        this.log.info("using executable: " + this.executable);
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

    public String cxGetResultsSummary(String scanID) throws IOException, CxException {
        return runResultExecutionCommands(scanID, "summaryHTML", ".html");
    }

    public String cxGetResultsList(String scanID) throws IOException, CxException {
        return runResultExecutionCommands(scanID, "json", ".json");
    }

    public void cxGetResults(String resultType, String scanID, String fileName, String target)
            throws IOException, CxException {

        List<String> commands = buildResultCommand(resultType, scanID, fileName, target);

        runResultExecutionCommands(commands);
    }

    private List<String> buildResultCommand(String resultType, String scanId, String outputName, String outputTarget)
            throws CxException {
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


    private String runResultExecutionCommands(String scanId, String resultType, String extension)
            throws IOException, CxException {
        Path tempDir = Files.createTempDirectory("cx");
        String fileName = Long.toString(System.nanoTime());
        List<String> commands = buildResultCommand(resultType, scanId, fileName, tempDir.toAbsolutePath().toString());
        runResultExecutionCommands(commands);

        File outputFile = new File(tempDir.toAbsolutePath().toString(), fileName + extension);
        return new String(Files.readAllBytes(Paths.get(outputFile.getAbsolutePath())), StandardCharsets.UTF_8);
    }

    public CxResultOutput cxGetResults(String scanId) throws IOException, CxException {
        String results = cxGetResultsList(scanId);
        return new ObjectMapper()
                .readerFor(CxResultOutput.class)
                .readValue(results);
    }

    private String runResultExecutionCommands(List<String> commands) throws IOException {
        log.info("Process submitting to the executor");
        Process process = Execution.executeCommand(commands);
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
        Process process = Execution.executeCommand(commands);
        String line;
        Scan scanObject;
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        CxCommandOutput cxCommandOutput = new CxCommandOutput();
        while ((line = br.readLine()) != null) {
            log.info(line);
            if (!StringUtils.isBlank(line) && isValidJSON(line)) {
                scanObject = transformToCxScanObject(line);
                List<Scan> scanList = new ArrayList<>();
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

    private Scan transformToCxScanObject(String line) {
        ObjectMapper objectMapper = new ObjectMapper();
        Scan scanObject;
        try {
            scanObject = objectMapper.readValue(line, new TypeReference<Scan>() {
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
        commands.add(baseUri);

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

    public CxValidateOutput cxAuthValidate() throws IOException, InterruptedException {
        log.info("Initialize auth validate command");
        List<String> commands = initialCommandsCommon();
        commands.add("auth");
        commands.add("validate");

        Process process = Execution.executeCommand(commands);

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        process.waitFor();

        CxValidateOutput cxValidateOutput = new CxValidateOutput();
        cxValidateOutput.setExitCode(process.exitValue());
        cxValidateOutput.setMessage(br.readLine());

        return cxValidateOutput;
    }

    public CxCommandOutput cxAstScanList() throws IOException, InterruptedException {
        log.info("Initialized scan list retrieval");
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("list");
        Process process = Execution.executeCommand(commands);
        String line;
        List<Scan> list = new ArrayList<>();
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

    public CxCommandOutput cxScanCreate(Map<CxParamType, String> params)
            throws IOException, InterruptedException, CxException {
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
        if (clientId != null && clientSecret != null) {
            commands.add("--client-id");
            commands.add(clientId);
            commands.add("--client-secret");
            commands.add(clientSecret);
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

    private static List<Scan> transformToCxScanList(String line) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Scan> scanList;
        try {
            scanList = objectMapper.readValue(line, new TypeReference<List<Scan>>() {
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
