package com.checkmarx.ast;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
        if (StringUtils.isBlank(this.cliConfig.getPathToExecutable())) {
            URL executableURL = getClass().getClassLoader().getResource(Execution.detectBinary());
            Objects.requireNonNull(executableURL, "could not find CLI executable");
            this.executable = executableURL.toURI();
        } else {
            File file = new File(this.cliConfig.getPathToExecutable());
            this.executable = file.toURI();
        }
        this.logger.info("using executable: " + executable);
    }

    public CLIOutput<Scan> scanShow(String scanId) throws IOException, InterruptedException {
        this.logger.info("initialized scan retrieval for id: {}", scanId);

        List<String> commands = commonArgumentsWithJSON();
        commands.add("scan");
        commands.add("show");
        commands.add("--scan-id");
        commands.add(scanId);

        return Execution.executeCommand(commands, Scan::scanFromLine);
    }

    public CLIOutput<List<Scan>> scanList(String filter) throws IOException, InterruptedException {
        this.logger.info("initialized retrieval for scan list: {}", filter);

        List<String> commands = commonArgumentsWithJSON();
        commands.add("scan");
        commands.add("list");
        commands.add("--filter");
        commands.add(filter);

        return Execution.executeCommand(commands, Scan::scanListFromLine);
    }

    public CLIOutput<Scan> scanCreate(Map<String, String> params, String additionalParameters)
            throws IOException, InterruptedException {
        this.logger.info("initialized scan create command");

        List<String> commands = commonArgumentsWithJSON();
        commands.add("scan");
        commands.add("create");

        for (Map.Entry<String, String> param : params.entrySet()) {
            commands.add(param.getKey());
            commands.add(param.getValue());
        }

        commands.addAll(CLIConfig.parseAdditionalParameters(additionalParameters));

        return Execution.executeCommand(commands, Scan::scanFromLine);
    }

    public CLIOutput<String> authValidate() throws IOException, InterruptedException {
        this.logger.info("initialized authentication validation command");

        List<String> commands = commonArguments();
        commands.add("auth");
        commands.add("validate");

        return Execution.executeTextCommand(commands);
    }

    private List<String> commonArguments() {
        List<String> commands = new ArrayList<>();

        commands.add(this.executable.getPath());
        commands.addAll(this.cliConfig.toArguments());

        return commands;
    }

    private List<String> commonArgumentsWithJSON() {
        List<String> commands = commonArguments();

        commands.add("--format");
        commands.add("json");

        return commands;
    }
}
