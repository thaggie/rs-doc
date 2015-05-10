package rs.doc.example;

public class Bar {

	private String id;
	private String baz;
	
	public Bar() {
		
	}
	
	public Bar(String id, String baz) {
		this.id = id;
		this.baz = baz;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getBaz() {
		return baz;
	}

	public void setBaz(String baz) {
		this.baz = baz;
	}
}
