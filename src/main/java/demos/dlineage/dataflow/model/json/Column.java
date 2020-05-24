package demos.dlineage.dataflow.model.json;

import com.alibaba.fastjson.annotation.JSONField;

public class Column {
	@JSONField(ordinal = 1)
	private String id;
	@JSONField(ordinal = 2)
	private String name;
	@JSONField(ordinal = 3)
	private Coordinate[] coordinates;
	@JSONField(ordinal = 4)
	private String qualifiedTable;
	@JSONField(ordinal = 5)
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
