package rs.doc.builder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;



class Endpoint {
	final String className;
	final String methodName;
	final String method;
	final String description;
	final String path;
	final List<Parameter> pathParameters;
	final List<Parameter> queryParameters;
	final List<Parameter> formParameters;
	final boolean deprecated;
	final List<String> produces;
	final Class<?> returnType;
	
	public Endpoint(String className, String methodName, String method, String description, String path, List<Parameter> pathParameters, List<Parameter> queryParameters, List<Parameter> formParameters, boolean deprecated, String[] produces, Class<?> returnType) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.method = method;
		this.description = description;
		this.path = path;
		this.pathParameters = pathParameters;
		this.queryParameters = queryParameters;
		this.formParameters = formParameters;
		this.deprecated = deprecated;
		this.produces = Arrays.asList(produces);
		this.returnType = returnType;
	}
	
	/**
	 * Compare two endpoints, first by name then by method
	 */
	static final Comparator<Endpoint> COMPARATOR = new Comparator<Endpoint>() {

		@Override
		public int compare(Endpoint o1, Endpoint o2) {
			int diff = String.CASE_INSENSITIVE_ORDER.compare(o1.path, o2.path);
			if (diff == 0) {
				diff = String.CASE_INSENSITIVE_ORDER.compare(o1.method, o2.method);
			}
			return diff;
		}
		
	}; 
}