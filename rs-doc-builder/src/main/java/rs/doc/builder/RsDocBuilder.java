package rs.doc.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;

import rs.doc.annotations.Doc;
import rs.doc.annotations.ResponseType;

public class RsDocBuilder {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		if (args.length < 2) {
			System.out.println("Usage: path/to/jar path/to/docs [client/project/path] [client.package.name]");
			return;
		}

		File jar = new File(args[0]);
		if (!jar.isFile()) {
			System.out.println(jar.getCanonicalFile() + " is not a file.");
		}

		File docsDir = new File(args[1]);
		if (!docsDir.isDirectory()) {
			System.out.println(docsDir.getCanonicalFile() + " is not a directory.");
		}
		
		List<String> classNames = getClasses(jar);
		URL[] urls = { jar.toURI().toURL() };
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		try (URLClassLoader child = new URLClassLoader(urls, RsDocBuilder.class.getClassLoader())) {
			for (String className : classNames) {
				try {
					Class<?> aClass = child.loadClass(className);
					inspect(aClass, endpoints);
				} catch (Throwable e) {
					// just quietly ignore any classes we can't load.
				}
			}
		}

		// The file generated is a the root of cirrus-services
		File docFile = new File(docsDir, "api.md");
		try (FileOutputStream fos = new FileOutputStream(docFile)) {
			MarkdownGenerator.generateMarkdown(endpoints, fos);
		}

		if (args.length >= 4) {
			File projectDir = new File(args[2]);
			if (!projectDir.isDirectory()) {
				System.out.println(projectDir.getCanonicalFile() + " is not a directory.");
			}
	
	
			String packageName = args[3];
			File clientOutputDir = new File(projectDir, "src/main/java/" + packageName.replace("\\.", "/"));
			clientOutputDir.mkdirs();
			BuilderGenerator.generateClasses(clientOutputDir, packageName, endpoints);
		}
	}

	private static List<String> getClasses(File jar) throws IOException {
		List<String> classes = new ArrayList<String>();
		try (JarFile jf = new JarFile(jar)) {
			Enumeration<JarEntry> entries = jf.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					String[] parts = name.split("/");
					String className = StringUtils.join(parts, '.');
					classes.add(className);
				}
			}
		}
		return classes;
	}

	/**
	 * Inspect a resource using reflection to identify its endpoints
	 * 
	 * @param resourceClass
	 * @param endpoints
	 */
	private static void inspect(Class<?> resourceClass, List<Endpoint> endpoints) {

		Path resourcePath = resourceClass.getAnnotation(Path.class);
		if (resourcePath == null) {
			return;
		}
		String className = resourceClass.getSimpleName();
		Method[] methods = resourceClass.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			Deprecated deprecated = method.getAnnotation(Deprecated.class);
			Path methodPath = method.getAnnotation(Path.class);
			Produces produces = method.getAnnotation(Produces.class);
			Doc doc = method.getAnnotation(Doc.class);
			ResponseType responseType = method.getAnnotation(ResponseType.class);
			String methodDescription = doc == null ? null : doc.value();
			if (methodPath != null) {
				// For each method we need to see which HTTP verbs it supports
				String get = method(method, GET.class, "GET");
				String post = method(method, POST.class, "POST");
				String put = method(method, PUT.class, "PUT");
				String delete = method(method, DELETE.class, "DELETE");
				String head = method(method, HEAD.class, "HEAD");
				Class<?> returnType = responseType == null ? method.getReturnType() : responseType.value();
				// only one of these will be non-null
				String[] verbs = { get, post, put, delete, head };

				for (String verb : verbs) {
					if (verb != null) {
						if (methodPath != null) {

							String path = resourcePath.value() + "/" + stripSlash(methodPath.value());

							Annotation[][] pass = method.getParameterAnnotations();
							Class<?>[] parameterTypes = method.getParameterTypes();
							List<Parameter> pathParameters = new ArrayList<Parameter>(parameterTypes.length);
							List<Parameter> queryParameters = new ArrayList<Parameter>(parameterTypes.length);
							List<Parameter> formParameters = new ArrayList<Parameter>(parameterTypes.length);

							for (int i = 0; i < pass.length; ++i) {
								Class<?> parameterType = parameterTypes[i];
								Annotation[] pas = pass[i];
								String description = findDoc(pas);
								for (Annotation pa : pas) {
									Class<? extends Annotation> type = pa.annotationType();
									if (type == PathParam.class) {
										PathParam qp = (PathParam) pa;
										pathParameters.add(new Parameter(qp.value(), description, parameterType));
									} else if (type == QueryParam.class) {
										QueryParam qp = (QueryParam) pa;
										queryParameters.add(new Parameter(qp.value(), description, parameterType));
									} else if (type == FormParam.class) {
										FormParam fp = (FormParam) pa;
										formParameters.add(new Parameter(fp.value(), description, parameterType));
									}
								}
							}

							endpoints.add(new Endpoint(className, methodName, verb, methodDescription, path, pathParameters,
									queryParameters, formParameters, deprecated != null, produces == null ? null : produces
											.value(), returnType));
						}
					}
				}
			}
		}
	}

	private static String findDoc(Annotation[] as) {
		for (Annotation a : as) {
			Class<? extends Annotation> type = a.annotationType();
			if (type == Doc.class) {
				Doc d = (Doc) a;
				return d.value();
			}
		}
		return null;
	}

	/**
	 * If the string starts with a slash remove that slash
	 * 
	 * @param value
	 * @return
	 */
	private static String stripSlash(String value) {
		return value.startsWith("/") ? value.substring(1) : value;
	}

	/**
	 * Get the name of the method if it's supported by the endpoint
	 * 
	 * @param method
	 * @param verbClass
	 * @param methodName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String method(Method method, Class verbClass, String methodName) {
		@SuppressWarnings("unchecked")
		Object o = method.getAnnotation(verbClass);
		return o == null ? null : methodName;
	}

}