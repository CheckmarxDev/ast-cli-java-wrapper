package com.checkmarx.ast.wrapper;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.scan.Scan;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.internal.NotNull;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

public class CxWrapper {

    @NonNull
    private final CxConfig cxConfig;
    @NonNull
    private final Logger logger;
    @NonNull
    private final URI executable;

    public CxWrapper(CxConfig cxConfig) throws CxConfig.InvalidCLIConfigException, URISyntaxException, IOException {
        this(cxConfig, LoggerFactory.getLogger(CxWrapper.class));
    }

    public CxWrapper(CxConfig cxConfig, Logger logger) throws CxConfig.InvalidCLIConfigException,
            URISyntaxException, IOException {
        if (cxConfig == null) {
            throw new CxConfig.InvalidCLIConfigException("configuration not supplied");
        }
        cxConfig.validate();
        this.cxConfig = cxConfig;
        this.logger = logger;
        this.executable = StringUtils.isBlank(this.cxConfig.getPathToExecutable())
                          ? Execution.detectBinary()
                          : new File(this.cxConfig.getPathToExecutable()).toURI();
        this.logger.info("using executable: " + executable);
    }

    public CxOutput<String> authValidate() throws IOException, InterruptedException {
        this.logger.info("initialized authentication validation command");

        List<String> arguments = commonArguments();
        arguments.add(CxConstants.CMD_AUTH);
        arguments.add(CxConstants.SUB_CMD_VALIDATE);

        return Execution.executeCommand(arguments, logger, (line) -> line);
    }

    public CxOutput<Scan> scanShow(@NotNull UUID scanId) throws IOException, InterruptedException {
        this.logger.info("initialized scan retrieval for id: {}", scanId);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_SHOW);
        arguments.add(CxConstants.SCAN_ID);
        arguments.add(scanId.toString());

        return Execution.executeCommand(arguments, logger, Scan::fromLine);
    }

    public CxOutput<List<Scan>> scanList() throws IOException, InterruptedException {
        return scanList("");
    }

    public CxOutput<List<Scan>> scanList(String filter) throws IOException, InterruptedException {
        this.logger.info("initialized retrieval for scan list {}", filter);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_LIST);
        if (StringUtils.isNotBlank(filter)) {
            arguments.add(CxConstants.FILTER);
            arguments.add(filter);
        }

        return Execution.executeCommand(arguments, logger, Scan::listFromLine);
    }

    public CxOutput<Scan> scanCreate(@NotNull Map<String, String> params) throws IOException, InterruptedException {
        return scanCreate(params, "");
    }

    public CxOutput<Scan> scanCreate(@NotNull Map<String, String> params, String additionalParameters)
            throws IOException, InterruptedException {
        this.logger.info("initialized scan create command");

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_CREATE);

        for (Map.Entry<String, String> param : params.entrySet()) {
            arguments.add(param.getKey());
            arguments.add(param.getValue());
        }

        arguments.addAll(CxConfig.parseAdditionalParameters(additionalParameters));

        return Execution.executeCommand(arguments, logger, Scan::fromLine);
    }

    public CxOutput<Project> projectShow(@NotNull UUID projectId) throws IOException, InterruptedException {
        this.logger.info("initialized project retrieval for id: {}", projectId);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_SHOW);
        arguments.add(CxConstants.PROJECT_ID);
        arguments.add(projectId.toString());

        return Execution.executeCommand(arguments, logger, Project::fromLine);
    }

    public CxOutput<List<Project>> projectList() throws IOException, InterruptedException {
        return projectList("");
    }

    public CxOutput<List<Project>> projectList(String filter) throws IOException, InterruptedException {
        this.logger.info("initialized retrieval for project list {}", filter);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_LIST);
        if (StringUtils.isNotBlank(filter)) {
            arguments.add(CxConstants.FILTER);
            arguments.add(filter);
        }

        return Execution.executeCommand(arguments, logger, Project::listFromLine);
    }

    public CxOutput<Results> results(@NotNull UUID scanId) throws IOException, InterruptedException {
        CxOutput<String> output = results(scanId, ReportFormat.json);
        Results results = null;
        if (output.getExitCode() == 0) {
            results = new ObjectMapper()
                    .readerFor(Results.class)
                    .readValue(output.getOutput());
        }
        return new CxOutput<>(output.getExitCode(), results);
    }

    public CxOutput<String> results(@NotNull UUID scanId, ReportFormat reportFormat)
            throws IOException, InterruptedException {
        this.logger.info("initialized results command {}", reportFormat);

        String tempDir = Files.createTempDirectory("cx").toAbsolutePath().toString();
        String fileName = Long.toString(System.nanoTime());

        List<String> arguments = commonArguments();
        arguments.add(CxConstants.CMD_RESULT);
        arguments.add(CxConstants.SCAN_ID);
        arguments.add(scanId.toString());
        arguments.add(CxConstants.REPORT_FORMAT);
        arguments.add(reportFormat.toString());
        arguments.add(CxConstants.OUTPUT_NAME);
        arguments.add(fileName);
        arguments.add(CxConstants.OUTPUT_PATH);
        arguments.add(tempDir);

        return Execution.executeCommand(arguments,
                                        logger, tempDir,
                                        fileName + reportFormat.getExtension());
    }

    private List<String> commonArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add(this.executable.getPath());
        arguments.addAll(this.cxConfig.toArguments());

        return arguments;
    }

    private List<String> jsonArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add(CxConstants.FORMAT);
        arguments.add(CxConstants.FORMAT_JSON);

        return arguments;
    }
}
