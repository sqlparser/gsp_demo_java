package demos.sqlenv.metadata.model;

public class Sequence {
	private String database;
	private String schema;
	private String name;
	private String incrementBy;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIncrementBy() {
		return incrementBy;
	}

	public void setIncrementBy(String incrementBy) {
		this.incrementBy = incrementBy;
	}
}