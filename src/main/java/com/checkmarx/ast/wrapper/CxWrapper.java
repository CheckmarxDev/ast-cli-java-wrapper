package com.checkmarx.ast.wrapper;

import com.checkmarx.ast.predicate.Predicate;
import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.results.ResultsSummary;
import com.checkmarx.ast.scan.Scan;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private final String executable;

    public CxWrapper(@NonNull CxConfig cxConfig)
            throws CxConfig.InvalidCLIConfigException, IOException {
        this(cxConfig, LoggerFactory.getLogger(CxWrapper.class));
    }

    public CxWrapper(@NonNull CxConfig cxConfig, @NonNull Logger logger) throws CxConfig.InvalidCLIConfigException,
            IOException {
        cxConfig.validate();
        this.cxConfig = cxConfig;
        this.logger = logger;
        this.executable = StringUtils.isBlank(this.cxConfig.getPathToExecutable())
                          ? Execution.getTempBinary()
                          : this.cxConfig.getPathToExecutable();
        this.logger.info("Executable path: " + executable);
    }

    public String authValidate() throws IOException, InterruptedException, CxException {
        this.logger.info("Executing 'auth validate' command using the CLI.");

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_AUTH);
        arguments.add(CxConstants.SUB_CMD_VALIDATE);

        return Execution.executeCommand(withConfigArguments(arguments), logger, (line) -> line);
    }

    public Scan scanShow(@NonNull UUID scanId) throws IOException, InterruptedException, CxException {
        this.logger.info("Retrieving the details for scan id: {}", scanId);

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
        this.logger.info("Fetching the scan list using the filter: {}", filter);

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
        this.logger.info("Executing 'scan create' command using the CLI.");

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_SCAN);
        arguments.add(CxConstants.SUB_CMD_CREATE);
        arguments.add(CxConstants.SCAN_INFO_FORMAT);
        arguments.add(CxConstants.FORMAT_JSON);

        for (Map.Entry<String, String> param : params.entrySet()) {
            arguments.add(param.getKey());
            arguments.add(param.getValue());
        }

        arguments.addAll(CxConfig.parseAdditionalParameters(additionalParameters));

        return Execution.executeCommand(withConfigArguments(arguments), logger, Scan::fromLine);
    }

    public List<Predicate> triageShow(@NonNull UUID projectId, String similarityId, String scanType) throws IOException, InterruptedException, CxException {
        this.logger.info("Executing 'triage show' command using the CLI.");
        this.logger.info("Fetching the list of predicates for projectId {} , similarityId {} and scan-type {}.,", projectId, similarityId, scanType);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_TRIAGE);
        arguments.add(CxConstants.SUB_CMD_SHOW);
        arguments.add(CxConstants.PROJECT_ID);
        arguments.add(projectId.toString());
        arguments.add(CxConstants.SIMILARITY_ID);
        arguments.add(similarityId);
        arguments.add(CxConstants.SCAN_TYPE);
        arguments.add(scanType);

        arguments.addAll(jsonArguments());

        return Execution.executeCommand(withConfigArguments(arguments), logger, Predicate::listFromLine);
    }

    public void triageUpdate(@NonNull UUID projectId, String similarityId, String scanType, String state, String comment, String severity) throws IOException, InterruptedException, CxException {
        this.logger.info("Executing 'triage update' command using the CLI.");
        this.logger.info("Updating the similarityId {} with state {} and severity {}.", similarityId, state, severity);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_TRIAGE);
        arguments.add(CxConstants.SUB_CMD_UPDATE);
        arguments.add(CxConstants.PROJECT_ID);
        arguments.add(projectId.toString());
        arguments.add(CxConstants.SIMILARITY_ID);
        arguments.add(similarityId);
        arguments.add(CxConstants.SCAN_TYPE);
        arguments.add(scanType);
        arguments.add(CxConstants.STATE);
        arguments.add(state);
        if(!StringUtils.isBlank(comment)) {
            arguments.add(CxConstants.COMMENT);
            arguments.add(comment);
        }
        arguments.add(CxConstants.SEVERITY);
        arguments.add(severity);

        Execution.executeCommand(withConfigArguments(arguments), logger, (line) -> null);
    }

    public Project projectShow(@NonNull UUID projectId) throws IOException, InterruptedException, CxException {
        this.logger.info("Retrieving the details for project id: {}", projectId);

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
        this.logger.info("Fetching the project list using the filter: {}", filter);

        List<String> arguments = new ArrayList<>();
        arguments.add(CxConstants.CMD_PROJECT);
        arguments.add(CxConstants.SUB_CMD_LIST);
        arguments.addAll(filterArguments(filter));
        arguments.addAll(jsonArguments());

        return Execution.executeCommand(withConfigArguments(arguments), logger, Project::listFromLine);
    }

    public List<String> projectBranches(@NonNull UUID projectId, String filter)
            throws CxException, IOException, InterruptedException {
        this.logger.info("Fetching the branches for project id {} using the filter: {}", projectId, filter);

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

    public ResultsSummary resultsSummary(@NonNull UUID scanId) throws IOException, InterruptedException, CxException {
        return new ObjectMapper()
                .readerFor(ResultsSummary.class)
                .readValue(results(scanId, ReportFormat.summaryJSON));
    }

    public Results results(@NonNull UUID scanId) throws IOException, InterruptedException, CxException {
        return new ObjectMapper()
                .readerFor(Results.class)
                .readValue(results(scanId, ReportFormat.json));
    }

    public String results(@NonNull UUID scanId, ReportFormat reportFormat)
            throws IOException, InterruptedException, CxException {
        this.logger.info("Retrieving the scan result for scan id {}", scanId);

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

        arguments.add(this.executable);
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
