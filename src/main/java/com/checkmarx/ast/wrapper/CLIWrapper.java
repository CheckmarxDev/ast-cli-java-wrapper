package com.checkmarx.ast.wrapper;

import com.checkmarx.ast.project.Project;
import com.checkmarx.ast.results.ReportFormat;
import com.checkmarx.ast.results.Results;
import com.checkmarx.ast.scan.Scan;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

public class CLIWrapper {

    @NonNull
    private final CLIConfig cliConfig;
    @NonNull
    private final Logger logger;
    @NonNull
    private final URI executable;

    public CLIWrapper(CLIConfig cliConfig) throws CLIConfig.InvalidCLIConfigException, URISyntaxException, IOException {
        this(cliConfig, LoggerFactory.getLogger(CLIWrapper.class));
    }

    public CLIWrapper(CLIConfig cliConfig, Logger logger) throws CLIConfig.InvalidCLIConfigException,
            URISyntaxException, IOException {
        Objects.requireNonNull(cliConfig, "configuration object not supplied");
        cliConfig.validate();
        this.cliConfig = cliConfig;
        this.logger = logger;
        this.executable = StringUtils.isBlank(this.cliConfig.getPathToExecutable())
                          ? Execution.detectBinary()
                          : new File(this.cliConfig.getPathToExecutable()).toURI();
        this.logger.info("using executable: " + executable);
    }

    public CLIOutput<String> authValidate() throws IOException, InterruptedException {
        this.logger.info("initialized authentication validation command");

        List<String> arguments = commonArguments();
        arguments.add(CLIConstants.CMD_AUTH);
        arguments.add(CLIConstants.SUB_CMD_VALIDATE);

        return Execution.executeCommand(arguments, (line) -> line);
    }

    public CLIOutput<Scan> scanShow(String scanId) throws IOException, InterruptedException {
        this.logger.info("initialized scan retrieval for id: {}", scanId);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CLIConstants.CMD_SCAN);
        arguments.add(CLIConstants.SUB_CMD_SHOW);
        arguments.add(CLIConstants.SCAN_ID);
        arguments.add(scanId);

        return Execution.executeCommand(arguments, Scan::fromLine);
    }

    public CLIOutput<List<Scan>> scanList() throws IOException, InterruptedException {
        return scanList("");
    }

    public CLIOutput<List<Scan>> scanList(String filter) throws IOException, InterruptedException {
        this.logger.info("initialized retrieval for scan list {}", filter);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CLIConstants.CMD_SCAN);
        arguments.add(CLIConstants.SUB_CMD_LIST);
        if (StringUtils.isNotBlank(filter)) {
            arguments.add(CLIConstants.FILTER);
            arguments.add(filter);
        }

        return Execution.executeCommand(arguments, Scan::listFromLine);
    }

    public CLIOutput<Scan> scanCreate(Map<String, String> params) throws IOException, InterruptedException {
        return scanCreate(params, "");
    }

    public CLIOutput<Scan> scanCreate(Map<String, String> params, String additionalParameters)
            throws IOException, InterruptedException {
        this.logger.info("initialized scan create command");

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CLIConstants.CMD_SCAN);
        arguments.add(CLIConstants.SUB_CMD_CREATE);

        for (Map.Entry<String, String> param : params.entrySet()) {
            arguments.add(param.getKey());
            arguments.add(param.getValue());
        }

        arguments.addAll(CLIConfig.parseAdditionalParameters(additionalParameters));

        return Execution.executeCommand(arguments, Scan::fromLine);
    }

    public CLIOutput<Project> projectShow(String projectId) throws IOException, InterruptedException {
        this.logger.info("initialized project retrieval for id: {}", projectId);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CLIConstants.CMD_PROJECT);
        arguments.add(CLIConstants.SUB_CMD_SHOW);
        arguments.add(CLIConstants.PROJECT_ID);
        arguments.add(projectId);

        return Execution.executeCommand(arguments, Project::fromLine);
    }

    public CLIOutput<List<Project>> projectList() throws IOException, InterruptedException {
        return projectList("");
    }

    public CLIOutput<List<Project>> projectList(String filter) throws IOException, InterruptedException {
        this.logger.info("initialized retrieval for project list {}", filter);

        List<String> arguments = commonArguments();
        arguments.addAll(jsonArguments());
        arguments.add(CLIConstants.CMD_PROJECT);
        arguments.add(CLIConstants.SUB_CMD_LIST);
        if (StringUtils.isNotBlank(filter)) {
            arguments.add(CLIConstants.FILTER);
            arguments.add(filter);
        }

        return Execution.executeCommand(arguments, Project::listFromLine);
    }

    public CLIOutput<Results> results(String scanId) throws IOException, InterruptedException {
        CLIOutput<String> output = results(scanId, ReportFormat.json);
        Results results = null;
        if (output.getExitCode() == 0) {
            results = new ObjectMapper()
                    .readerFor(Results.class)
                    .readValue(output.getOutput());
        }
        return new CLIOutput<>(output.getExitCode(), results);
    }

    public CLIOutput<String> results(String scanId, ReportFormat reportFormat)
            throws IOException, InterruptedException {
        this.logger.info("initialized results command {}", reportFormat);

        String tempDir = Files.createTempDirectory("cx").toAbsolutePath().toString();
        String fileName = Long.toString(System.nanoTime());

        List<String> arguments = commonArguments();
        arguments.add(CLIConstants.CMD_RESULT);
        arguments.add(CLIConstants.SCAN_ID);
        arguments.add(scanId);
        arguments.add(CLIConstants.REPORT_FORMAT);
        arguments.add(reportFormat.toString());
        arguments.add(CLIConstants.OUTPUT_NAME);
        arguments.add(fileName);
        arguments.add(CLIConstants.OUTPUT_PATH);
        arguments.add(tempDir);

        return Execution.executeCommand(arguments,
                                        tempDir,
                                        fileName + reportFormat.getExtension());
    }

    private List<String> commonArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add(this.executable.getPath());
        arguments.addAll(this.cliConfig.toArguments());

        return arguments;
    }

    private List<String> jsonArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add(CLIConstants.FORMAT);
        arguments.add(CLIConstants.FORMAT_JSON);

        return arguments;
    }

    public static final class CommandException extends Exception {
        public CommandException(String message) {
            super(message);
        }
    }
}
