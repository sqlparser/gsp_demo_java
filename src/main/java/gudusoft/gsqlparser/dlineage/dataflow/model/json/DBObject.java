package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class DBObject {
	private String id;
	private String database;
	private String schema;
	private String name;
	private String alias;
	private String type;
	private String tableType;
	private Argument[] arguments;
	private Column[] columns;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Argument[] getArguments() {
		return arguments;
	}

	public void setArguments(Argument[] arguments) {
		this.arguments = arguments;
	}

	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinate[] coordinates) {
		this.coordinates = coordinates;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	
}
