package com.checkmarx.ast.scanhandler;

import com.checkmarx.ast.exceptionhandler.CxException;
import com.checkmarx.ast.executionservice.ExecutionService;
import com.checkmarx.ast.resultshandler.CxResultFormatType;
import com.checkmarx.ast.resultshandler.CxResultOutput;
import com.checkmarx.ast.resultshandler.CxResultType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final URI executable;
    private static final Gson gson = new Gson();

    public CxAuth(CxScanConfig scanConfig, Logger log) throws IOException, URISyntaxException, CxException {
        if (scanConfig == null)
            throw new CxException("CxScanConfig object returned as null!");

        this.baseuri = scanConfig.getBaseUri();
        this.baseAuthUri = scanConfig.getBaseAuthUri();
        this.tenant = scanConfig.getTenant();
        this.key = scanConfig.getClientId();
        this.secret = scanConfig.getClientSecret();
        this.apikey = scanConfig.getApiKey();

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
            fileURI = URI.create(jarLocation.toString() + fileName);
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
        if (scanObject.getScanObjectList() != null && scanObject.getScanObjectList().size() == 1)
            log.info("Scan retrieved");
        else
            log.info("Did not receive the scan");

        return scanObject;
    }

    public CxResultOutput cxGetResults(CxResultType resultType, String scanID, CxResultFormatType formatType,
            String target) throws IOException, InterruptedException {
        log.info("Inititalized result retrieval for id: {}", scanID);
        CxResultOutput resultOutput = null;
        List<String> commands = initialCommandsCommon();
        commands.add("result");
        commands.add(resultType.name().toLowerCase());
        commands.add("--scan-id");
        commands.add(scanID);
        if (formatType != null) {
            commands.add("--format");
            commands.add(formatType.name().toLowerCase());
        }

        if (resultType.equals(CxResultType.SUMMARY)) {
            ProcessBuilder tempProcess = new ProcessBuilder(commands);
            tempProcess.redirectErrorStream(true);
            File file = new File(target != null ? target : System.getProperty("user.dir") + "/cx-ast-results.html");
            tempProcess.redirectOutput(file);
            Process proc = tempProcess.start();
            proc.waitFor();
            if (!proc.isAlive())
                log.info("Exit code from CLI: {}", proc.exitValue());
            proc.destroy();
        } else {
            ExecutionService exec = new ExecutionService();
            Process process = exec.executeCommand(commands);
            String line;
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                log.info(line);
                if (isJSONValid(line, CxResultOutput.class)) {
                    resultOutput = transformToCxResultOutputObject(line);

                }
            }

        }
        return resultOutput;
    }

    private CxResultOutput transformToCxResultOutputObject(String line) {
        ObjectMapper objectMapper = new ObjectMapper();
        CxResultOutput resultObject;
        try {
            resultObject = objectMapper.readValue(line, new TypeReference<CxResultOutput>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
        return resultObject;
    }

    private CxCommandOutput runExecutionCommands(List<String> commands) throws IOException, InterruptedException {
        log.info("Process submitting to the executor");
        ExecutionService exec = new ExecutionService();
        Process process = exec.executeCommand(commands);
        String line;
        CxScan scanObject = null;
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        CxCommandOutput cxCommandOutput = new CxCommandOutput();
        while ((line = br.readLine()) != null) {
            log.info(line);
            if (!StringUtils.isBlank(line) && isJSONValid(line, CxScan.class)) {
                scanObject = transformToCxScanObject(line);
                List<CxScan> scanList = new ArrayList<>();
                scanList.add(scanObject);
                cxCommandOutput.setScanObjectList(scanList);
            }
        }
        br.close();
        process.waitFor();
        if(!process.isAlive()) {
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
            if (isJSONValid(line, List.class) && !line.isEmpty())
                list = transformToCxScanList(line);
        }
        br.close();
        process.waitFor();

        CxCommandOutput cxCommandOutput = new CxCommandOutput();
        cxCommandOutput.setScanObjectList(list);
        cxCommandOutput.setExitCode(process.exitValue());
        if (list != null && !list.isEmpty())
            log.info("Retrieved scan list with size: {}", list.size());
        else
            log.info("Not able to retrieve scan list");

        return cxCommandOutput;
    }

    public CxCommandOutput cxScanCreate(Map<CxParamType, String> params) throws IOException, InterruptedException {
        log.info("Initialized scan creation");
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("create");

        for (Map.Entry<CxParamType, String> param : params.entrySet()) {
            if (param.getKey() == CxParamType.ADDITIONAL_PARAMETERS && param.getValue() != null) {
                addIndividualParams(commands, param.getValue());
            } else if (param.getKey().toString().length() == 1) {
                commands.add("-" + param.getKey().toString().toLowerCase());
                if (param.getValue() != null)
                    commands.add(param.getValue());
                else
                    commands.add(" ");

            } else if (param.getKey() != CxParamType.ADDITIONAL_PARAMETERS) {
                String paramValue = param.getKey().toString();
                paramValue = "--" + paramValue.replace("_", "-").toLowerCase();
                commands.add(paramValue);
                if (param.getValue() != null)
                    commands.add(param.getValue());
                else
                    commands.add(" ");

            }
        }

        return runExecutionCommands(commands);
    }

    private void addIndividualParams(List<String> commands, String value) {
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(value);
        while (m.find())
            commands.add(m.group(1));
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

    private List<CxScan> transformToCxScanList(String line) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CxScan> scanList = null;
        try {
            scanList = objectMapper.readValue(line, new TypeReference<List<CxScan>>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
        return scanList;

    }

    private boolean isJSONValid(String jsonInString, Object object) {
        try {
            gson.fromJson(jsonInString, (Type) object);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

}
