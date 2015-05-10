package rs.doc.builder;

class Parameter {
	final String name;
	final String description;
	final Class<?> type;
	public Parameter(String name, String description, Class<?> type) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return name;
	}
}