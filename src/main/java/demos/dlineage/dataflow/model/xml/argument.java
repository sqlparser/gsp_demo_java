package demos.dlineage.dataflow.model.xml;

import org.simpleframework.xml.Attribute;

public class argument {
	@Attribute(required = false)
	private String id;
	@Attribute(required = false)
	private String name;
	@Attribute(required = false)
	private String coordinate;
	@Attribute(required = false)
	private String datatype;
	@Attribute(required = false)
	private String inout;

	public argument() {
	}

	public String getCoordinate() {
		return this.coordinate;
	}
	
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getInout() {
		return this.inout;
	}

	public void setInout(String inout) {
		this.inout = inout;
	}
}
