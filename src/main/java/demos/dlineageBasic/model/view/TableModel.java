package demos.dlineageBasic.model.view;

import java.util.ArrayList;
import java.util.List;

public class TableModel {
	private String schema;
	private String name;
	private String alias;
	private String highlightInfo;
	private String coordinate;

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	private List<ColumnModel> columns = new ArrayList<ColumnModel>();

	public String getHighlightInfo() {
		return highlightInfo;
	}

	public void setHighlightInfo(String highlightInfo) {
		this.highlightInfo = highlightInfo;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ColumnModel[] getColumns() {
		return columns.toArray(new ColumnModel[0]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		if (schema != null)
			return schema + "." + name;
		else
			return name;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void addColumn(ColumnModel column) {
		if (column != null && !columns.contains(column))
			columns.add(column);
	}

	public void reset() {
		schema = null;
		name = null;
		columns.clear();
	}

	public boolean containsColumn(ColumnModel column) {
		return column.getTable() == this && getColumn(column.getName()) != null;
	}

	public ColumnModel getColumn(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			ColumnModel column = columns.get(i);
			if (columnName != null && columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableModel other = (TableModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		return true;
	}

}
