package com.checkmarx.ast;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class ExecutionService {
	public BufferedReader executeCommand(List<String> commands)
			throws IOException {
		ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		lmBuilder.redirectErrorStream(true);
		final Process lmProcess = lmBuilder.start();
		InputStream is = lmProcess.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;

	}

}
