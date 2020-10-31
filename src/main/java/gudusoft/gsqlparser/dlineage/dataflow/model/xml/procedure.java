package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "database", "schema", "name", "type", "coordinate", "arguments" })
public class procedure {

	private String id;

	private String database;

	private String schema;

	private String name;

	private String type;

	private String coordinate;

	private List<argument> arguments;

	public procedure() {
	}

	@XmlAttribute(required = false)
	public String getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	@XmlAttribute(required = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required = false)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(required = false)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "argument", required = false)
	public List<argument> getArguments() {
		if (this.arguments == null) {
			this.arguments = new LinkedList<argument>();
		}
		return this.arguments;
	}

	public void setArguments(List<argument> arguments) {
		this.arguments = arguments;
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

}
