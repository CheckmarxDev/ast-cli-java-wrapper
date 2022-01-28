package com.checkmarx.ast.wrapper;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CxThinWrapper {

    @NonNull
    private final Logger logger;
    @NonNull
    private final String executable;

    public CxThinWrapper() throws IOException {
        this(LoggerFactory.getLogger(CxWrapper.class));
    }

    public CxThinWrapper(@NonNull Logger logger) throws IOException {
        this.logger = logger;
        this.executable = Execution.getTempBinary();
        this.logger.info("Executable path: {} ", executable);
    }

    public String run(@NonNull String arguments) throws CxException, IOException, InterruptedException {
        this.logger.info("Executing commands with thin wrapper.");
        List<String> argv = new ArrayList<>();
        argv.add(executable);
        argv.addAll(Arrays.asList(arguments.split(" ")));
        return Execution.executeCommand(argv, logger, line -> line);
    }
}
