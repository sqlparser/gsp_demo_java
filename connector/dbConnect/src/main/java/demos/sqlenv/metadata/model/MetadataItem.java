package demos.sqlenv.metadata.model;

public class MetadataItem {
	private String databaseColumn;
	private String schemaColumn;
	private String tableNameColumn;
	private String isViewColumn;
	private String columnNameColumn;
	private String columnDataTypeColumn;
	private String columnCommentColumn;
	
	public String getDatabaseColumn() {
		return databaseColumn;
	}
	public void setDatabaseColumn(String databaseColumn) {
		this.databaseColumn = databaseColumn;
	}
	public String getSchemaColumn() {
		return schemaColumn;
	}
	public void setSchemaColumn(String schemaColumn) {
		this.schemaColumn = schemaColumn;
	}
	public String getTableNameColumn() {
		return tableNameColumn;
	}
	public void setTableNameColumn(String tableNameColumn) {
		this.tableNameColumn = tableNameColumn;
	}
	public String getIsViewColumn() {
		return isViewColumn;
	}
	public void setIsViewColumn(String isViewColumn) {
		this.isViewColumn = isViewColumn;
	}
	public String getColumnNameColumn() {
		return columnNameColumn;
	}
	public void setColumnNameColumn(String columnNameColumn) {
		this.columnNameColumn = columnNameColumn;
	}
	public String getColumnDataTypeColumn() {
		return columnDataTypeColumn;
	}
	public void setColumnDataTypeColumn(String columnDataTypeColumn) {
		this.columnDataTypeColumn = columnDataTypeColumn;
	}
	public String getColumnCommentColumn() {
		return columnCommentColumn;
	}
	public void setColumnCommentColumn(String columnCommentColumn) {
		this.columnCommentColumn = columnCommentColumn;
	}
   
}