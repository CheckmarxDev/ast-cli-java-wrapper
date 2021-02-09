package com.cx.sdk;

import org.apache.commons.lang3.EnumUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class CxAuth {

	// pass the argument as an enum

	private Map<String, String> keys = new HashMap<String, String>();
	private String baseuri;
	private final URI exe;

	public CxAuth(String authType) throws IOException, URISyntaxException {
		this.exe = packageExecutable();
		if (EnumUtils.isValidEnum(CxAuthType.class, authType.toUpperCase())) {
			authRequest(authType, null, null, null);
		} else {
			System.out.println(
					"Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
		}

	}

	public CxAuth(String authType, String username, String password,
			String baseurl) throws IOException, URISyntaxException {
		this.exe = packageExecutable();
		if (EnumUtils.isValidEnum(CxAuthType.class, authType.toUpperCase())) {
			authRequest(authType, username, password, baseurl);
		} else {
			System.out.println(
					"Invalid Auth Type. Valid ones are TOKEN, KEYSECRET, ENVIRONMENT");
		}

	}

	private void authRequest(String authType, String username, String password,
			String baseuri) throws IOException, URISyntaxException {

		if (authType.equalsIgnoreCase("Token")) {
			// token based auth implmentation
		}
		if (authType.equalsIgnoreCase("KeySecret")) {
			List<String> commands = new ArrayList<String>();
			System.out.println("OS: " + System.getProperty("os.name"));
			this.baseuri = baseuri;
			try {

				if (username != null && password != null && baseuri != null) {
					String command = exe.getPath() + " " + "auth register -u"
							+ username + " -p " + password + " --base-uri "
							+ baseuri;
					String[] list = command.split(" ");
					for (String str : list) {
						commands.add(str);
					}
					ExecutionService execute = new ExecutionService();
					BufferedReader br = execute.executeCommand(commands);
					String line;
					while ((line = br.readLine()) != null) {
						// extract the keys and write them to the credentials
						// file
						keys.put(line.split("=")[0], line.split("=")[1]);
					}
					if (keys.size() == 2) {
						System.out.println(
								"Received the key and secret. Continue with other calls");
						// JSONObject writeKeys = new JSONObject();
						// String key = (String) keys.get(0);
						// String secret = (String) keys.get(1);
						// writeKeys.put(key.split("=")[0], key.split("=")[1]);
						// writeKeys.put(secret.split("=")[0],
						// secret.split("=")[1]);
						// FileWriter file = new FileWriter("keys.json", false);
						// file.write(writeKeys.toJSONString());
						// file.flush();
					} else {
						System.out.println(
								"Did not receive the Key and secret. Please check and try again");
					}
				}
				// System.out.println(commands);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private URI packageExecutable() throws IOException, URISyntaxException {
		// InputStream is = CxAuth.class.getResourceAsStream(
		// System.getProperty("user.dir") + "ast.exe");
		// File exeFile = new File("./ast.exe");
		// FileOutputStream fos = new FileOutputStream(exeFile);
		// byte bytes[] = new byte[1000];
		// int k = 0;
		// while ((k = is.read(bytes)) != -1) {
		// fos.write(bytes, 0, k);
		// }
		// fos.close();

		URI uri = getJarURI();
		URI exe = getFile(uri, "cx.exe");
		System.out.println(exe);
		return exe;

	}

	private static URI getJarURI() throws URISyntaxException {
		final ProtectionDomain domain;
		final CodeSource source;
		final URL url;
		final URI uri;

		domain = CxAuth.class.getProtectionDomain();
		source = domain.getCodeSource();
		url = source.getLocation();
		uri = url.toURI();

		return (uri);
	}

	private static URI getFile(final URI where, final String fileName)
			throws ZipException, IOException {
		final File location;
		final URI fileURI;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {
			final ZipFile zipFile;

			zipFile = new ZipFile(location);

			try {
				fileURI = extract(zipFile, fileName);
			} finally {
				zipFile.close();
			}
		}

		return (fileURI);
	}

	private static URI extract(final ZipFile zipFile, final String fileName)
			throws IOException {
		final File tempFile;
		final ZipEntry entry;
		final InputStream zipStream;
		OutputStream fileStream;

		tempFile = File.createTempFile(fileName,
				Long.toString(System.currentTimeMillis()));
		tempFile.deleteOnExit();
		entry = zipFile.getEntry(fileName);

		if (entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName
					+ " in archive: " + zipFile.getName());
		}

		zipStream = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			final byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while ((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}

		return (tempFile.toURI());
	}

	private static void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void scanShow(String id) {

	}

	public void cxAstScanList() throws IOException, InterruptedException {
		List commands = new ArrayList<String>();
		commands.add(exe.getPath());
		commands.add("scan");
		commands.add("list");
		commands.add("--key=" + keys.get("CX_AST_ACCESS_KEY_ID"));
		commands.add("--secret=" + keys.get("CX_AST_ACCESS_KEY_SECRET"));
		commands.add("--base-uri=" + baseuri);
		// ProcessBuilder lmBuilder = new ProcessBuilder(commands);
		// lmBuilder.redirectErrorStream(true);
		// final Process lmProcess = lmBuilder.start();
		// // int result = lmProcess.waitFor(); // result becomes 0
		// InputStream is = lmProcess.getInputStream();
		// InputStreamReader isr = new InputStreamReader(is);
		ExecutionService exec = new ExecutionService();
		BufferedReader br = exec.executeCommand(commands);
		List<String> resultList = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line.replace("Â", " "));
			resultList.add(line.replace("Â", " "));

		}

		if (resultList.size() == 0) {
			System.out.println("No scans found");
		}
		// resultList.remove(0);
		// transformToCxScanObject(resultList);

	}

	public void cxScanCreate() {

	}

	// private void transformToCxScanObject(List<String> resultList) {
	// // TODO Auto-generated method stub
	// List<CxScan> scanList = new ArrayList<CxScan>();
	// for (String result : resultList) {
	// String[] ind = result.split(" ");
	// CxScan cx = new CxScan();
	// cx.(ind[0]);
	// cx.setProjectID(ind[1]);
	// cx.setStatus(ind[2]);
	// cx.setCreatedAt(ind[3]);
	// cx.setUpdatedAt(ind[4]);
	// cx.setTags(ind[5]);
	// cx.setInitiator(ind[6]);
	// cx.setOrigin(ind[7]);
	// scanList.add(cx);
	// }
	// }

}
