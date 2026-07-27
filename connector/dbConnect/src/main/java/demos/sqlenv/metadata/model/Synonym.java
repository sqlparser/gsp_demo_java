package demos.sqlenv.metadata.model;

public class Synonym {
	private String database;
	private String schema;
	private String name;
	private String sourceName;
	private String sourceSchema;
	private String sourceDbLinkName;

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

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceSchema() {
		return sourceSchema;
	}

	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	public String getSourceDbLinkName() {
		return sourceDbLinkName;
	}

	public void setSourceDbLinkName(String sourceDbLinkName) {
		this.sourceDbLinkName = sourceDbLinkName;
	}

}