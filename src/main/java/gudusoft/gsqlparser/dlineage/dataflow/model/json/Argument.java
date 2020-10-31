package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class Argument {
	private String id;
	private String name;
	private String datatype;
	private String inout;
	private Coordinate[] coordinates;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getInout() {
		return inout;
	}

	public void setInout(String inout) {
		this.inout = inout;
	}

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinate[] coordinates) {
		this.coordinates = coordinates;
	}

}
