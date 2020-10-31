
package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gudusoft.gsqlparser.dlineage.util.Pair;

@XmlType(propOrder = { "id", "name", "coordinate", "source", "qualifiedTable", "isFunction" })
public class column {

	private String id;

	private String name;

	private String coordinate;

	private String source;

	private String qualifiedTable;

	private String isFunction;

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
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@XmlAttribute(required = false)
	public String getQualifiedTable() {
		return qualifiedTable;
	}

	public void setQualifiedTable(String qualifiedTable) {
		this.qualifiedTable = qualifiedTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((qualifiedTable == null) ? 0 : qualifiedTable.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		column other = (column) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (qualifiedTable == null) {
			if (other.qualifiedTable != null)
				return false;
		} else if (!qualifiedTable.equals(other.qualifiedTable))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@XmlAttribute(name = "isFunction", required = false)
	public String getIsFunction() {
		return isFunction;
	}

	public void setIsFunction(String isFunction) {
		this.isFunction = isFunction;
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
