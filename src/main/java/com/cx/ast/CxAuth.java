package com.cx.ast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

@Log
public class CxAuth {

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
            this.executable = new URI(pathToExecutable);
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
            authRequest(authType, baseuri);
        } else {
            log.info(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    private URI checkIfConfigExists() throws IOException, InterruptedException, URISyntaxException {
        File fileLocation = new File("./.checkmarx");
        URI uri = null;
        if(!fileLocation.exists()) {
            fileLocation.mkdirs();
            //download the file from GitHub
            downloadExecutables(fileLocation.toURI(),"cx.exe");
            uri = new URI("./.checkmarx/cx.exe");
        }
        return uri;
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

            authRequest(authType, baseuri);
        } else {
            log.info(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    private void authRequest(CxAuthType authType, String baseuri) {
        log.info("CONTINUE WITH THE CALLS!");
    }

    private URI packageExecutable() throws IOException, URISyntaxException, InterruptedException {
        String osName = System.getProperty("os.name");

        URI uri = getJarURI();
        log.info("getURI location: " + uri);
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

    private static URI getJarURI() throws URISyntaxException {
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

    private static URI getFile( URI where, final String fileName)
            throws IOException, InterruptedException {
        final File location;
        final URI fileURI;
        location = new File(where);

        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
            log.info("FILE URI: " + fileURI);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
                log.info("FILE URI: " + fileURI);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static void downloadExecutables(URI user_dir, String file) throws IOException, InterruptedException {
//        String link =
//                "https://github.com/CheckmarxDev/ast-cli/releases/download/v1.0.0_RC3/" + file;
//        String            fileName = file;
//        URL               url  = new URL( link );
//        HttpURLConnection http = (HttpURLConnection)url.openConnection();
//        Map< String, List< String >> header = http.getHeaderFields();
//        while( isRedirected( header )) {
//            link = header.get( "Location" ).get( 0 );
//            url    = new URL( link );
//            http   = (HttpURLConnection)url.openConnection();
//            header = http.getHeaderFields();
//        }
//        InputStream  input  = http.getInputStream();
//        byte[]       buffer = new byte[4096];
//        int          n      = -1;
//        OutputStream output = new FileOutputStream( new File( user_dir + file ));
//        while ((n = input.read(buffer)) != -1) {
//            output.write( buffer, 0, n );
//        }
//        output.close();
        List<String> commands = new ArrayList<>();
        commands.add("curl");
        commands.add("-H");
        commands.add("'Authorization: token 4a15d3330f198712cf4f50ff55fedf85e0f68532'");
        commands.add("-H");
        commands.add("'Accept: application/vnd.github.v3.raw'");
       // commands.add("--create-dirs");
        commands.add("-o");
        commands.add(user_dir.getPath());
        commands.add("-O");
        commands.add("-L");
        commands.add("https://github.com/CheckmarxDev/ast-cli/releases/download/v1.0.0_RC3/" + file);
        for(String command:commands){
            log.info(command);
        }
        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        while ((line = br.readLine()) != null) {
            log.info(line.replace("Â", " "));

        }
    }

    private static boolean isRedirected( Map<String, List<String>> header ) {
        for( String hv : header.get( null )) {
            if(   hv.contains( " 301 " )
                    || hv.contains( " 302 " )) return true;
        }
        return false;
    }

    private static URI extract(final ZipFile zipFile, final String fileName)
            throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;
        String user_dir = "./.checkmarx";
        File file = new File(user_dir);
        if(!file.exists())
            file.mkdirs();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        log.info(dateFormat.format(date));
        //tempFile = File.createTempFile(fileName,
          //      dateFormat.format(date),file);
        tempFile = new File(file + fileName +dateFormat.format(date));
        //tempFile.deleteOnExit();
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
        List commands = new ArrayList<String>();
        commands.add(executable.getPath());
        commands.add("scan");
        commands.add("show");
        commands.add(id);
        commands = addAuthCredentials(commands);
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");
        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        CxScan scanObject = null;
        while ((line = br.readLine()) != null) {
            log.info(line.replace("Â", " "));
            if(isJSONValid(line))
            scanObject = transformToCxScanObject(line);
        }
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

    public List<CxScan> cxAstScanList() throws IOException, InterruptedException {
        List commands = new ArrayList<String>();
        commands.add(executable.getPath());
        commands.add("scan");
        commands.add("list");
        commands = addAuthCredentials(commands);
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");

        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        List<CxScan> list = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            log.info(line.replace("Â", " "));
            if(isJSONValid(line))
            list = transformToCxScanList(line);
        }

        return list;

    }

    public CxScan cxScanCreate(Map<CxParamType, String> params) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add(executable.getPath());
        commands.add("scan");
        commands.add("create");
        commands = addAuthCredentials(commands);
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");

        for (Map.Entry<CxParamType, String> param : params.entrySet()) {
            if(param.getKey().toString().length() == 1 ) {
                commands.add("-" + param.getKey().toString().toLowerCase());
                commands.add(param.getValue());

            }
            else {
                String paramValue = param.getKey().toString();
                paramValue = "--" + paramValue.replace("_","-").toLowerCase();
                commands.add(paramValue);
                commands.add(param.getValue());

            }
        }

        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        CxScan scanObject = null;
        while ((line = br.readLine()) != null) {
            log.info(line.replace("Â", " "));
            if (isJSONValid(line))
                scanObject = transformToCxScanObject(line);
        }
        return scanObject;
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

    private boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

}


