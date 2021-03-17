package com.checkmarx.ast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.EnumUtils;
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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class CxAuth {
    private static Logger log = LoggerFactory.getLogger(CxAuth.class.getName());
    private String baseuri;
    private String key;
    private String secret;
    private String token;
    private  URI executable = null;
    private static final Gson gson = new Gson();

    public CxAuth( CxAuthType authType,String baseuri, String key, String secret, String pathToExecutable) throws IOException, URISyntaxException, InterruptedException {
        if(pathToExecutable == null || pathToExecutable.isEmpty()) {
            this.executable = packageExecutable();
        }
        else {
            File file = new File(pathToExecutable);
            this.executable = file.toURI();
        }
        //this.executable = checkIfConfigExists();
        this.baseuri = baseuri;
        if (EnumUtils.isValidEnum(CxAuthType.class, authType.name())) {
            if(authType.equals(CxAuthType.KEYSECRET)){
                if(key != null && secret != null) {
                    this.key = key;
                    this.secret = secret;
                }
                else{
                    log.info("Key or secret is null, please check and try again");
                }
            }
        } else {
            log.info(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    public CxAuth(CxScanConfig scanConfig, Logger log) throws InterruptedException, IOException, URISyntaxException {
        if(scanConfig != null) {
            this.baseuri = scanConfig.getBaseuri();
            if(scanConfig.getKey() != null && scanConfig.getSecret() != null) {
                this.key = scanConfig.getKey();
                this.secret = scanConfig.getSecret();
            }
            else if (scanConfig.getToken() != null) {
                this.token = scanConfig.getToken();
            }
            else {
                log.info("Did not receive Key/Secret/Token");
            }
            if(scanConfig.getPathToExecutable() != null && !scanConfig.getPathToExecutable().isEmpty()) {
                File file = new File(scanConfig.getPathToExecutable());
                this.executable = file.toURI();
            }
            else {
                this.executable = packageExecutable();
            }
        }
        if(log != null) {
            this.log = log;
        }
    }


    public CxAuth(CxAuthType authType, String baseuri, String token, String pathToExecutable) throws IOException, URISyntaxException, InterruptedException {
        if(pathToExecutable == null || pathToExecutable.isEmpty()) {
            this.executable = packageExecutable();
        }
        else {
            this.executable = URI.create(pathToExecutable);
        }
        this.baseuri = baseuri;
        if (EnumUtils.isValidEnum(CxAuthType.class, authType.name())) {
            if(authType.equals(CxAuthType.TOKEN)) {
                if(token != null) {
                    this.token = token;
                }
                else {
                    log.info("Token not present");
                }
            }
        } else {
            log.info(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    private URI packageExecutable() throws IOException, URISyntaxException, InterruptedException {
        String osName = System.getProperty("os.name");

        URI uri = getJarURI();
        URI executable = null;
        if (osName.toLowerCase().contains("windows")) {
            executable = getFile(uri, "cx.exe");
        } else if (osName.toLowerCase().contains("mac")) {
            executable = getFile(uri, "cx-mac");
        } else {
            executable = getFile(uri, "cx-exe");
        }
        log.info(executable + " ");
        return executable;

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

    private URI getFile( URI jarLocation, final String fileName)
            throws IOException, InterruptedException {
        final File location;
        final URI fileURI;
        location = new File(jarLocation);

        if (location.isDirectory()) {
            fileURI = URI.create(jarLocation.toString() + fileName);
            log.info("Location of the jar file: " + fileURI);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
                log.info("Location of the jar file: " + fileURI);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static URI extract(final ZipFile zipFile, final String fileName)
            throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        //tempFile = new File(fileName);
        tempFile = File.createTempFile(fileName," ");
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName
                    + " in archive: " + zipFile.getName());
        }

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];
            i = 0;

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

    public CxScan cxScanShow(String id) throws IOException, InterruptedException {
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("show");
        commands.add(id);
        CxScan scanObject = runExecutionCommands(commands);
        log.info("In create scan method:end");
        return scanObject;
    }

    private CxScan runExecutionCommands(List<String> commands) throws IOException, InterruptedException {
        log.info("In run execution commands:start");
        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        CxScan scanObject = null;
        while ((line = br.readLine()) != null) {
            log.info(line);
            if(isJSONValid(line,CxScan.class))
                scanObject = transformToCxScanObject(line);
        }
        log.info("In run execution commands:end");
        return scanObject;
    }

    private CxScan transformToCxScanObject(String line)  {
        ObjectMapper objectMapper = new ObjectMapper();
        CxScan scanObject = null;
        try {
            scanObject = objectMapper.readValue(line, new TypeReference<CxScan>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
        return scanObject;
    }

    public List<String> initialCommands() {
        List commands = new ArrayList<String>();
        commands.add(executable.getPath());
        commands = addAuthCredentials(commands);
        commands.add("--base-uri");
        commands.add(baseuri);
        commands.add("--format");
        commands.add("json");
        return commands;
    }

    public List<CxScan> cxAstScanList() throws IOException, InterruptedException {
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("list");

        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        List<CxScan> list = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            log.info(line.replace("Â", " "));
            if(isJSONValid(line,List.class) && !line.isEmpty())
                list = transformToCxScanList(line);
        }
        br.close();

        return list;

    }

    public CxScan cxScanCreate(Map<CxParamType, String> params) throws IOException, InterruptedException {
        log.info("In create scan method:start");
        List<String> commands = initialCommands();
        commands.add("scan");
        commands.add("create");

        for (Map.Entry<CxParamType, String> param : params.entrySet()) {
            if(param.getKey() == CxParamType.ADDITIONAL_PARAMETERS){
                commands = addIndividualParams(commands,param.getValue());
               // commands.add(param.getValue());
            }
            else if(param.getKey().toString().length() == 1 ) {
                commands.add("-" + param.getKey().toString().toLowerCase());
                if(param.getValue() != null)
                    commands.add(param.getValue());
                else
                    commands.add(" ");

            }
            else {
                String paramValue = param.getKey().toString();
                paramValue = "--" + paramValue.replace("_","-").toLowerCase();
                commands.add(paramValue);
                if(param.getValue() != null)
                    commands.add(param.getValue());
                else
                    commands.add(" ");

            }
        }

        CxScan scanObject = runExecutionCommands(commands);
        return scanObject;
    }

    private List<String> addIndividualParams(List<String> commands, String value) {
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(value);
        while(m.find())
            commands.add(m.group(1));
        return commands;
    }

    private List<String> addAuthCredentials(List<String> commands) {
        if(key != null && secret != null) {
            commands.add("--key");
            commands.add(key);
            commands.add("--secret");
            commands.add(secret);
        }
        else if(token != null) {
            commands.add("--token");
            commands.add(token);
        }
        else {
            log.info("KEY/SECRET/TOKEN not received");
        }
        return commands;
    }

    private List<CxScan> transformToCxScanList(String line) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CxScan> scanList = null;
        try {
            scanList = objectMapper.readValue(line, new TypeReference<List<CxScan>>() {
            });
        }
        catch(JsonProcessingException e) {
            return null;
        }
        return scanList;

    }

    private boolean isJSONValid(String jsonInString , Object object) {
        try {
            gson.fromJson(jsonInString, (Type) object);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

}


