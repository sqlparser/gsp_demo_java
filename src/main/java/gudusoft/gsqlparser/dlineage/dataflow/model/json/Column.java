package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class Column {
	private String id;
	private String name;
	private Coordinate[] coordinates;
	private String qualifiedTable;
	private String source;

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

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinate[] coordinates) {
		this.coordinates = coordinates;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQualifiedTable() {
		return qualifiedTable;
	}

	public void setQualifiedTable(String qualifiedTable) {
		this.qualifiedTable = qualifiedTable;
	}

}
