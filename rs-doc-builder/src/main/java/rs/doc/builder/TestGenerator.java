package rs.doc.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;

public class TestGenerator {

	public static void generateClasses(File outputDir, List<Endpoint> endpoints) throws IOException {
		
		for (Endpoint endpoint : endpoints) {
			generateTestClass(endpoint, outputDir);
		}		
	}

	private static void generateTestClass(Endpoint endpoint, File outputDir) throws IOException {
		
		String className = generateClassName(endpoint);
		
		File outputFile = new File(outputDir, className + ".java");
		if (!outputFile.exists()) {
			StringBuilder sb = new StringBuilder();
			sb.append("package com.dnb.cirrus.api.test;\n\n");
			
			sb.append("import org.junit.Test;\n\n");
			
			sb.append("public class ");
			
			sb.append(className);
			sb.append(" {\n");
			
			sb.append("\n\t@Test\n");
			sb.append("\tpublic void todo() {\n");
			sb.append("\t\t//TODO: Integration test for /services" + endpoint.path);
			sb.append("\n");
			sb.append("\t}\n");
			
			sb.append("}\n");

			FileUtils.writeStringToFile(outputFile, sb.toString(), "UTF-8");
		}
	}
	
	private static String generateClassName(Endpoint endpoint) {
		StringBuilder sb = new StringBuilder();
		if (endpoint.className.endsWith("Resource")) {
			sb.append(endpoint.className.substring(0, endpoint.className.length()-"Resource".length()));
		} else {
			sb.append(endpoint.className);
		}
		sb.append(WordUtils.capitalize(endpoint.methodName));
		
		sb.append("Test");
		
		return sb.toString();
	}
}
