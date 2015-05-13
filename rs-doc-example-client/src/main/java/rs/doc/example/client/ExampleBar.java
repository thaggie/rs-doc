/**
 * This is a generated file DO NOT EDIT
 */
package rs.doc.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This method really raises the bar.
 * GET /foo/bar/{id}
 * @return application/json
 * @author rs.doc.builder.BuilderGenerator
 */
public class ExampleBar {

	ExampleBar(String path) {
		this.method = HttpMethod.GET;
		this.path = path;
	}

	public static ExampleBar create(String id) {
		String path = "/foo/bar/{id}";
		path = path.replace("{id}", String.valueOf(id));
		return new ExampleBar(path);
	}

	public ExampleBar baz(String baz) {
		addParameter(queryParameters, "baz", baz);
		return this;
	}
 

	private final HttpMethod method; 
	private final String path;
	private final Map<String,List<String>> queryParameters = new HashMap<String,List<String>>();
	private final Map<String,List<String>> formParameters = new HashMap<String,List<String>>();
	
	void addParameter(Map<String,List<String>> parameters, String name, String value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			} else {
				list.clear();
			}
			list.add(value);
		}
	}

	void addParameter(Map<String,List<String>> parameters, String name, Integer value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			} else {
				list.clear();
			}
			list.add(value.toString());
		}
	}

	void addParameter(Map<String,List<String>> parameters, String name, Long value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			} else {
				list.clear();
			}
			list.add(value.toString());
		}
	}
	
	void addParameter(Map<String,List<String>> parameters, String name, Double value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			} else {
				list.clear();
			}
			list.add(value.toString());
		}
	}
	
	void addParameter(Map<String,List<String>> parameters, String name, Boolean value) {
		if (value == null) {
			parameters.remove(name);
		} else {
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			} else {
				list.clear();
			}
			list.add(value.toString());
		}
	}
	
	void addParameter(Map<String,List<String>> parameters, String name, List<String> values) {
		List<String> list = parameters.get(name);
		if (list == null) {
			list = new ArrayList<String>();
			parameters.put(name, list);
		}
		list.addAll(values);
	}
	
	public HttpClientRequest<ByteBuf> build() {
		String uri = buildUri();
		HttpClientRequest<ByteBuf> request = HttpClientRequest.create(method, uri);
		
		if (formParameters.size() > 0) {
			StringBuilder sb = new StringBuilder();
			writeParameters(formParameters, sb);
			String content = sb.toString();
			request.withContent(content);
			request.withHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		
		return request;
	}
	
	/**
	 * Builds the uri using the path and parameters
	 * @return String 
	 */
	private String buildUri() {
		StringBuilder sb = new StringBuilder("/services");
		sb.append(path);
		if (queryParameters.size() > 0) {
			sb.append("?");
			writeParameters(queryParameters, sb);
		}
		return sb.toString();
	}
	
	private static void writeParameters(Map<String,List<String>> parameters, StringBuilder sb) {
		boolean first = true;
		for (Entry<String,List<String>> entry : parameters.entrySet()) {
			String parameter = entry.getKey();
			List<String> values = entry.getValue();
			if (values == null || values.size() == 0) {
				if (!first) {
					sb.append("&");
				} else {
					first = false;
				}
				sb.append(parameter);
			} else {
				for (String value : values) {
					if (!first) {
						sb.append("&");
					} else {
						first = false;
					}
					sb.append(parameter);
					sb.append("=");
					try {
						sb.append(URLEncoder.encode(value, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			}
			first = false;
		}
	}

}
