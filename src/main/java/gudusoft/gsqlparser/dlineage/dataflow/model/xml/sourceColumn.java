package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gudusoft.gsqlparser.dlineage.dataflow.model.ModelBindingManager;
import gudusoft.gsqlparser.dlineage.util.SQLUtil;

@XmlType(propOrder = { "id", "column", "value", "source_id", "source_name", "column_type", "parent_id", "parent_name",
		"coordinate", "clauseType", "source" })
public class sourceColumn {

	private String id;

	private String column;

	private String value;

	private String source_id;

	private String source_name;

	private String column_type;

	private String parent_id;

	private String parent_name;

	private String coordinate;

	private String clauseType;

	private String source;

	@XmlAttribute(required = false)
	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	@XmlAttribute(required = false)
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	@XmlAttribute(required = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "parent_id", required = false)
	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	@XmlAttribute(name = "parent_name", required = false)
	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	@XmlAttribute(required = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlAttribute(name = "source_name", required = false)
	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	@XmlAttribute(name = "source_id", required = false)
	public String getSource_id() {
		return source_id;
	}

	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}

	@XmlAttribute(required = false)
	public String getClauseType() {
		return clauseType;
	}

	public void setClauseType(String clauseType) {
		this.clauseType = clauseType;
	}

	@XmlAttribute(name = "column_type", required = false)
	public String getColumn_type() {
		return column_type;
	}

	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}

	@XmlAttribute(required = false)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		String columnName = getColumnName(column);
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((parent_id == null) ? 0 : parent_id.hashCode());
		return result;
	}

	private String getColumnName(String columnName) {
		if (ModelBindingManager.getGlobalVendor() != null) {
			return SQLUtil.getIdentifierNormalName(columnName);
		}

		return columnName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		sourceColumn other = (sourceColumn) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!getColumnName(column).equals(getColumnName(other.column)))
			return false;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (parent_id == null) {
			if (other.parent_id != null)
				return false;
		} else if (!parent_id.equals(other.parent_id))
			return false;
		return true;
	}

}
