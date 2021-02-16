package com.cx.sdk;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.EnumUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class CxAuth {

    // pass the argument as an enum

    private final Map<String, String> keys = new HashMap<>();
    private String baseuri;
    private final URI executable;
    private static final Gson gson = new Gson();

    public CxAuth( CxAuthType authType) throws IOException, URISyntaxException {
        this.executable = packageExecutable();
        if (EnumUtils.isValidEnum(CxAuthType.class, authType.name())) {
            authRequest(authType, null, null, null);
        } else {
            System.out.println(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    public CxAuth(CxAuthType authType, String username, String password,
                  String baseurl) throws IOException, URISyntaxException {
        this.executable = packageExecutable();
        if (EnumUtils.isValidEnum(CxAuthType.class, authType.name())) {
            authRequest(authType, username, password, baseurl);
        } else {
            System.out.println(
                    "Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
        }

    }

    private void authRequest(CxAuthType authType, String username, String password,
                             String baseuri) {

        if (authType.name() == "TOKEN") {
            // token based auth implmentation
        }
        if (authType.name() == "KEYSECRET") {
            List<String> commands = new ArrayList<>();
            System.out.println("OS: " + System.getProperty("os.name"));
            this.baseuri = baseuri;
            try {

                if (username != null && password != null && baseuri != null) {
                    String command = executable.getPath() + " " + "auth register -u"
                            + username + " -p " + password + " --base-uri "
                            + baseuri;
                    String[] list = command.split(" ");
                    Collections.addAll(commands, list);
                    ExecutionService execute = new ExecutionService();
                    BufferedReader br = execute.executeCommand(commands);
                    String line;
                    while ((line = br.readLine()) != null) {
                        // extract the keys and write them to the credentials
                        // file
                        if(line.contains("CX_AST_ACCESS_KEY_ID") || line.contains("CX_AST_ACCESS_KEY_SECRET"))
                        keys.put(line.split("=")[0], line.split("=")[1]);
                        else {
                            System.out.println(line);
                        }
                    }
                    if (keys.size() == 2) {
                        System.out.println(
                                "Received the key and secret. Continue with other calls");
                        // JSONObject writeKeys = new JSONObject();
                        // String key = (String) keys.get(0);
                        // String secret = (String) keys.get(1);
                        // writeKeys.put(key.split("=")[0], key.split("=")[1]);
                        // writeKeys.put(secret.split("=")[0],
                        // secret.split("=")[1]);
                        // FileWriter file = new FileWriter("keys.json", false);
                        // file.write(writeKeys.toJSONString());
                        // file.flush();
                    } else {
                        System.out.println(
                                "Did not receive the Key and secret. Please check and try again");
                    }
                }
                // System.out.println(commands);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private URI packageExecutable() throws IOException, URISyntaxException {
        // InputStream is = CxAuth.class.getResourceAsStream(
        // System.getProperty("user.dir") + "ast.exe");
        // File exeFile = new File("./ast.exe");
        // FileOutputStream fos = new FileOutputStream(exeFile);
        // byte bytes[] = new byte[1000];
        // int k = 0;
        // while ((k = is.read(bytes)) != -1) {
        // fos.write(bytes, 0, k);
        // }
        // fos.close();
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
        System.out.println(executable);
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

    private static URI getFile(final URI where, final String fileName)
            throws IOException {
        final File location;
        final URI fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
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

        tempFile = File.createTempFile(fileName,
                Long.toString(System.currentTimeMillis()));
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
        List commands = new ArrayList<String>();
        commands.add(executable.getPath());
        commands.add("scan");
        commands.add("show");
        commands.add(id);
        commands.add("--key=" + keys.get("CX_AST_ACCESS_KEY_ID"));
        commands.add("--secret=" + keys.get("CX_AST_ACCESS_KEY_SECRET"));
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");
        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        CxScan scanObject = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line.replace("Â", " "));
            if(isJSONValid(line))
            scanObject = transformToCxScanObject(line);
            //resultList.add(line.replace("Â", " "));
            //resultList.add(transformToCxScanObject(line));
            //list = transformToCxScanObject(line);
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
        commands.add("--key=" + keys.get("CX_AST_ACCESS_KEY_ID"));
        commands.add("--secret=" + keys.get("CX_AST_ACCESS_KEY_SECRET"));
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");
        // ProcessBuilder lmBuilder = new ProcessBuilder(commands);
        // lmBuilder.redirectErrorStream(true);
        // final Process lmProcess = lmBuilder.start();
        // // int result = lmProcess.waitFor(); // result becomes 0
        // InputStream is = lmProcess.getInputStream();
        // InputStreamReader isr = new InputStreamReader(is);
        ExecutionService exec = new ExecutionService();
        BufferedReader br = exec.executeCommand(commands);
        String line;
        List<CxScan> list = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            System.out.println(line.replace("Â", " "));

            //resultList.add(line.replace("Â", " "));
            //resultList.add(transformToCxScanObject(line));
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
        commands.add("--key=" + keys.get("CX_AST_ACCESS_KEY_ID"));
        commands.add("--secret=" + keys.get("CX_AST_ACCESS_KEY_SECRET"));
        commands.add("--base-uri=" + baseuri);
        commands.add("--format=json");

        for (Map.Entry<CxParamType, String> param : params.entrySet()) {
            if(param.getKey() == CxParamType.S || param.getKey() == CxParamType.V) {
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
            System.out.println(line.replace("Â", " "));
            if (isJSONValid(line))
                scanObject = transformToCxScanObject(line);
        }
        return scanObject;
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


