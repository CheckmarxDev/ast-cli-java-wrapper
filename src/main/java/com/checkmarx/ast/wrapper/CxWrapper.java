package com.checkmarx.ast.wrapper;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.scan.Scan;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CxWrapper {

    private static final CollectionType BRANCHES_TYPE = TypeFactory.defaultInstance()
                                                                   .constructCollectionType(List.class, String.class);

    @NonNull
    private final CxConfig cxConfig;
    @NonNull
    private final Logger logger;
    @NonNull
    private final URI executable;

    public CxWrapper(@NonNull CxConfig cxConfig)
            throws CxConfig.InvalidCLIConfigException, URISyntaxException, IOException {
        this(cxConfig, LoggerFactory.getLogger(CxWrapper.class));
    }

    public CxWrapper(@NonNull CxConfig cxConfig, @NonNull Logger logger) throws CxConfig.InvalidCLIConfigException,
            URISyntaxException, IOException {
        cxConfig.validate();
        this.cxConfig = cxConfig;
        this.logger = logger;
        this.executable = StringUtils.isBlank(this.cxConfig.getPathToExecutable())
                          ? Execution.detectBinary()
                          : new File(this.cxConfig.getPathToExecutable()).toURI();
        this.logger.info("using executable: " + executable);
    }

    public String authValidate() throws IOException, InterruptedException, CxException {
        this.logger.info("initialized authentication validation command");

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_AUTH);
        arguments.add(CxConstants.SUB_CMD_VALIDATE);

        return Execution.executeCommand(withConfigArguments(arguments), logger, (line) -> line);
    }

    public Scan scanShow(@NonNull UUID scanId) throws IOException, InterruptedException, CxException {
        this.logger.info("initialized scan retrieval for id: {}", scanId);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_SHOW);
        arguments.add(CxConstants.SCAN_ID);
        arguments.add(scanId.toString());
        arguments.addAll(jsonArguments());

        return Execution.executeCommand(withConfigArguments(arguments), logger, Scan::fromLine);
    }

    public List<Scan> scanList() throws IOException, InterruptedException, CxException {
        return scanList("");
    }

    public List<Scan> scanList(String filter) throws IOException, InterruptedException, CxException {
        this.logger.info("initialized retrieval for scan list {}", filter);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_LIST);
        arguments.addAll(jsonArguments());
        arguments.addAll(filterArguments(filter));

        return Execution.executeCommand(withConfigArguments(arguments), logger, Scan::listFromLine);
    }

    public Scan scanCreate(@NonNull Map<String, String> params) throws IOException, InterruptedException, CxException {
        return scanCreate(params, "");
    }

    public Scan scanCreate(@NonNull Map<String, String> params, String additionalParameters)
            throws IOException, InterruptedException, CxException {
        this.logger.info("initialized scan create command");

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_CREATE);
        arguments.addAll(jsonArguments());

        for (Map.Entry<String, String> param : params.entrySet()) {
            arguments.add(param.getKey());
            arguments.add(param.getValue());
        }

        arguments.addAll(CxConfig.parseAdditionalParameters(additionalParameters));

        return Execution.executeCommand(withConfigArguments(arguments), logger, Scan::fromLine);
    }

    public Project projectShow(@NonNull UUID projectId) throws IOException, InterruptedException, CxException {
        this.logger.info("initialized project retrieval for id: {}", projectId);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_SHOW);
        arguments.add(CxConstants.PROJECT_ID);
        arguments.add(projectId.toString());
        arguments.addAll(jsonArguments());

        return Execution.executeCommand(withConfigArguments(arguments), logger, Project::fromLine);
    }

    public List<Project> projectList() throws IOException, InterruptedException, CxException {
        return projectList("");
    }

    public List<Project> projectList(String filter) throws IOException, InterruptedException, CxException {
        this.logger.info("initialized retrieval for project list {}", filter);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_LIST);
        arguments.addAll(filterArguments(filter));
        arguments.addAll(jsonArguments());

        return Execution.executeCommand(withConfigArguments(arguments), logger, Project::listFromLine);
    }

    public List<String> projectBranches(@NonNull UUID projectId, String filter)
            throws CxException, IOException, InterruptedException {
        this.logger.info("initialized retrieval for project branches {}", filter);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_BRANCHES);
        arguments.add(CxConstants.PROJECT_ID);
        arguments.add(projectId.toString());
        arguments.addAll(filterArguments(filter));

        return Execution.executeCommand(withConfigArguments(arguments),
                                        logger,
                                        (line) -> CxBaseObject.parse(line, BRANCHES_TYPE));
    }

    public Results results(@NonNull UUID scanId) throws IOException, InterruptedException, CxException {
        return new ObjectMapper()
                .readerFor(Results.class)
                .readValue(results(scanId, ReportFormat.json));
    }

    public String results(@NonNull UUID scanId, ReportFormat reportFormat)
            throws IOException, InterruptedException, CxException {
        this.logger.info("initialized results command {}", reportFormat);

        String tempDir = Files.createTempDirectory("cx").toAbsolutePath().toString();
        String fileName = Long.toString(System.nanoTime());

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_RESULT);
        arguments.add(CxConstants.SCAN_ID);
        arguments.add(scanId.toString());
        arguments.add(CxConstants.REPORT_FORMAT);
        arguments.add(reportFormat.toString());
        arguments.add(CxConstants.OUTPUT_NAME);
        arguments.add(fileName);
        arguments.add(CxConstants.OUTPUT_PATH);
        arguments.add(tempDir);

        return Execution.executeCommand(withConfigArguments(arguments),
                                        logger, tempDir,
                                        fileName + reportFormat.getExtension());
    }

    private List<String> withConfigArguments(List<String> commands) {
        List<String> arguments = new ArrayList<>();

        arguments.add(this.executable.getPath());
        arguments.addAll(commands);
        arguments.addAll(this.cxConfig.toArguments());

        return arguments;
    }

    private List<String> jsonArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add(CxConstants.FORMAT);
        arguments.add(CxConstants.FORMAT_JSON);

        return arguments;
    }

    private List<String> filterArguments(String filter) {
        List<String> arguments = new ArrayList<>();

        if (StringUtils.isNotBlank(filter)) {
            arguments.add(CxConstants.FILTER);
            arguments.add(filter);
        }

        return arguments;
    }
}
