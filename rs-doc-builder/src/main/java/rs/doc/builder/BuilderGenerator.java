package rs.doc.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;



/**
 * This class generates builder classes for making requests against the API
 */
public class BuilderGenerator {

	public static void generateClasses(File outputDir, String packageName, List<Endpoint> endpoints) throws IOException {
		
		String classTemplate = loadTemplate();
		classTemplate = classTemplate.replace("{package-name}", packageName);
		for (Endpoint endpoint : endpoints) {
			generateClass(endpoint, classTemplate, outputDir);
		}

	}
	
	private static void generateClass(Endpoint endpoint, String classTemplate, File outputDir) throws IOException {
		String className = generateClassName(endpoint);
			
		StringBuilder generatedCode = new StringBuilder();
		generatedCode.append("/**\n");
		if (endpoint.description != null) {
			generatedCode.append(" * ");
			generatedCode.append(endpoint.description);
			generatedCode.append("\n");
		}
		generatedCode.append(" * ");
		generatedCode.append(endpoint.method);
		generatedCode.append(" ");
		generatedCode.append(endpoint.path);
		generatedCode.append("\n");
		if (endpoint.produces != null) {
			generatedCode.append(" * @return ");
			for (int i=0; i<endpoint.produces.size(); ++i) {
				if (i > 0) {
					generatedCode.append(", ");
				}
				String produces = endpoint.produces.get(i);
				generatedCode.append(produces);	
			}
			generatedCode.append("\n");
		}
		generatedCode.append(" * @author ");
		generatedCode.append(BuilderGenerator.class.getName());
		generatedCode.append("\n");
		generatedCode.append(" */\n");
		generatedCode.append("public class ");
		generatedCode.append(className);
		generatedCode.append(" {\n\n");
		generatedCode.append("\t");
		generatedCode.append(className);
		generatedCode.append("(String path) {\n");
		generatedCode.append("\t\tthis.method = HttpMethod.");
		generatedCode.append(endpoint.method);
		generatedCode.append(";\n");
		generatedCode.append("\t\tthis.path = path;\n");
		generatedCode.append("\t}\n");
		
		generatedCode.append("\n");
		
		generatedCode.append("\tpublic static ");
		generatedCode.append(className);
		generatedCode.append(" create(");
		for (int i=0; i<endpoint.pathParameters.size(); ++i) {
			if (i > 0) {
				generatedCode.append(", ");
			}
			Parameter pathParameter = endpoint.pathParameters.get(i);
			String type = pathParameter.type.getSimpleName();
			generatedCode.append(type);
			generatedCode.append(" ");
			generatedCode.append(pathParameter.name);
			
		}
		generatedCode.append(") {\n");
		generatedCode.append("\t\tString path = \"");
		generatedCode.append(endpoint.path);
		generatedCode.append("\";\n");
		
		for (Parameter pathParameter : endpoint.pathParameters) {
			generatedCode.append("\t\tpath = path.replace(\"{");
			generatedCode.append(pathParameter.name);
			generatedCode.append("}\", String.valueOf(");
			generatedCode.append(pathParameter.name);
			generatedCode.append("));\n");
		}
		
		generatedCode.append("\t\treturn new ");
		generatedCode.append(className);
		generatedCode.append("(path);\n");
		generatedCode.append("\t}\n");
		
		for (Parameter queryParameter : endpoint.queryParameters) {
			generateParameter(className, "queryParameters", queryParameter, generatedCode);
		}

		for (Parameter formParameter : endpoint.formParameters) {
			generateParameter(className, "formParameters", formParameter, generatedCode);
		}

		String classFile = classTemplate.replace("{generated-code}", generatedCode);
		File outputFile = new File(outputDir, className + ".java");
		FileUtils.writeStringToFile(outputFile, classFile, "UTF-8");
	}

	private static void generateParameter(String className, String parameterType, Parameter parameter, StringBuilder generatedCode) {
		generatedCode.append("\n\tpublic ");
		generatedCode.append(className);
		generatedCode.append(" ");
		generatedCode.append(parameter.name);
		generatedCode.append("(");
		String type = parameter.type.getSimpleName();
		boolean isList = "List".equals(type); 
		if (isList) {
			generatedCode.append("List<String>");
		} else {
			generatedCode.append(type);
		}
		generatedCode.append(" ");
		generatedCode.append(parameter.name);
		generatedCode.append(") {\n");
		generatedCode.append("\t\taddParameter(");
		generatedCode.append(parameterType);
		generatedCode.append(", \"");
		generatedCode.append(parameter.name);
		generatedCode.append("\", ");
		generatedCode.append(parameter.name);
		generatedCode.append(");\n");
		generatedCode.append("\t\treturn this;\n");
		generatedCode.append("\t}\n");
	}

	private static String generateClassName(Endpoint endpoint) {
		StringBuilder sb = new StringBuilder();
		if (endpoint.className.endsWith("Resource")) {
			sb.append(endpoint.className.substring(0, endpoint.className.length()-"Resource".length()));
		} else {
			sb.append(endpoint.className);
		}
		sb.append(WordUtils.capitalize(endpoint.methodName));
		return sb.toString();
	}

	private static String loadTemplate() throws IOException {
		try (InputStream is = BuilderGenerator.class.getResourceAsStream("/api-endpoint-builder.txt")) {
			String classTemplate = IOUtils.toString(is);
			return classTemplate;
		}
		
	}
}
