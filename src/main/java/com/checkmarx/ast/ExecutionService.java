package com.checkmarx.ast;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class ExecutionService {
	private static Logger log = LoggerFactory.getLogger(ExecutionService.class.getName());
	public BufferedReader executeCommand(List<String> commands)
			throws IOException, InterruptedException {
		log.info("In ExecutionService, executeCommand:start");
		ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		lmBuilder.redirectErrorStream(true);
		final Process lmProcess = lmBuilder.start();
		//int result = lmProcess.waitFor(); // result becomes 0
		InputStream is = lmProcess.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		log.info("In ExecutionService, executeCommand:end");
		return br;

	}

}
