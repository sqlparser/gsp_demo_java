package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "column", "function", "target_id", "target_name", "parent_id", "parent_name", "coordinate",
		"source" })
public class targetColumn {

	private String id;

	private String column;

	private String function;

	private String target_id;

	private String target_name;

	private String parent_id;

	private String parent_name;

	private String coordinate;

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

	@XmlAttribute(name="parent_id", required = false)
	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	@XmlAttribute(name="parent_name", required = false)
	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	@XmlAttribute(required = false)
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@XmlAttribute(name="target_id", required = false)
	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}

	@XmlAttribute(name="target_name", required = false)
	public String getTarget_name() {
		return target_name;
	}

	public void setTarget_name(String target_name) {
		this.target_name = target_name;
	}

	@XmlAttribute(required = false)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
