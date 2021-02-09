package com.cx.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ExecutionService {

	public BufferedReader executeCommand(List commands)
			throws IOException, InterruptedException {

		// List commands = new ArrayList<String>();
		// commands.add(System.getProperty("user.dir")+ "\\ast.exe");
		ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		lmBuilder.redirectErrorStream(true);
		final Process lmProcess = lmBuilder.start();
		int result = lmProcess.waitFor(); // result becomes 0
		InputStream is = lmProcess.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		// String line;
		// while ((line = br.readLine()) != null) {
		// System.out.println(line);
		// }
		return br;

	}

}
