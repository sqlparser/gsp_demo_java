package gudusoft.gsqlparser.dlineage.dataflow.metadata.model;

public class MetadataRelation {
	private String sourceDb;
	private String sourceSchema;
	private String sourceTable;
	private String sourceColumn;
	private String targetDb;
	private String targetSchema;
	private String targetTable;
	private String targetColumn;
	private String procedureName;
	private String queryName;
	public String getSourceDb() {
		return sourceDb;
	}
	public void setSourceDb(String sourceDb) {
		this.sourceDb = sourceDb;
	}
	public String getSourceSchema() {
		return sourceSchema;
	}
	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}
	public String getSourceTable() {
		return sourceTable;
	}
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}
	public String getSourceColumn() {
		return sourceColumn;
	}
	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}
	public String getTargetDb() {
		return targetDb;
	}
	public void setTargetDb(String targetDb) {
		this.targetDb = targetDb;
	}
	public String getTargetSchema() {
		return targetSchema;
	}
	public void setTargetSchema(String targetSchema) {
		this.targetSchema = targetSchema;
	}
	public String getTargetTable() {
		return targetTable;
	}
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}
	public String getTargetColumn() {
		return targetColumn;
	}
	public void setTargetColumn(String targetColumn) {
		this.targetColumn = targetColumn;
	}
	public String getProcedureName() {
		return procedureName;
	}
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
