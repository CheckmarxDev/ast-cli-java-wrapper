package com.checkmarx.ast.executionservice;

import java.io.IOException;
import java.util.List;

public class ExecutionService {
	public Process executeCommand(List<String> commands) throws IOException {
		ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		lmBuilder.redirectErrorStream(true);
		return lmBuilder.start();
	}


	public Integer executeCommandSync(List<String> commands) throws IOException, InterruptedException {
		ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		lmBuilder.redirectErrorStream(true);
		final Process process = lmBuilder.start();

		return process.waitFor();
	}
}
