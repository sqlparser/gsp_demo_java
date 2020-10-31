package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gudusoft.gsqlparser.dlineage.util.Pair;
import gudusoft.gsqlparser.dlineage.util.SQLUtil;

@XmlType(propOrder = { "id", "database", "schema", "name", "alias", "type", "tableType", "isTarget",
		"coordinate", "columns", "parent" })
public class table {

	private String id;

	private String database;

	private String schema;

	private String name;

	private String alias;

	private String type;

	private String tableType;

	private String isTarget;

	private String coordinate;

	private List<column> columns;

	private String parent;

	@XmlAttribute(required = false)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@XmlElement(name = "column", required = false)
	public List<column> getColumns() {
		if (this.columns == null) {
			this.columns = new LinkedList<column>();
		}
		return columns;
	}

	public void setColumns(List<column> columns) {
		this.columns = columns;
	}

	@XmlAttribute(required = false)
	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	@XmlAttribute(required = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(required = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isView() {
		return "view".equals(type);
	}

	public boolean isTable() {
		return "table".equals(type);
	}

	public boolean isResultSet() {
		return type != null && !isView() && !isTable();
	}

	@XmlAttribute(name = "isTarget", required = false)
	public boolean getIsTarget() {
		return "true".equals(isTarget);
	}

	@XmlAttribute(required = false)
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@XmlAttribute(required = false)
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	@XmlAttribute(required = false)
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@XmlAttribute(required = false)
	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		if (!SQLUtil.isEmpty(database)) {
			fullName.append(database).append(".");
		}
		if (!SQLUtil.isEmpty(schema)) {
			fullName.append(schema).append(".");
		}
		if (fullName.length() > 0) {
			int index = name.lastIndexOf(".");
			if (index != -1) {
				fullName.append(name.substring(index + 1));
			} else {
				fullName.append(name);
			}
		} else {
			fullName.append(name);
		}
		return fullName.toString();
	}

	public String getTableNameOnly() {
		int index = name.lastIndexOf(".");
		if (index != -1) {
			return name.substring(index + 1);
		} else {
			return name;
		}
	}

	public void setIsTarget(String isTarget) {
		this.isTarget = isTarget;
	}

	public int getOccurrencesNumber() {
		return PositionUtil.getOccurrencesNumber(coordinate);
	}

	public Pair<Integer, Integer> getStartPos(int index) {
		return PositionUtil.getStartPos(coordinate, index);
	}

	public Pair<Integer, Integer> getEndPos(int index) {
		return PositionUtil.getEndPos(coordinate, index);
	}

}
