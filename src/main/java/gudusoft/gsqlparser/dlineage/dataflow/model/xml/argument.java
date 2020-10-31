package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "name", "coordinate", "datatype", "inout" })
public class argument {

	private String id;

	private String name;

	private String coordinate;

	private String datatype;

	private String inout;

	public argument() {
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
	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@XmlAttribute(required = false)
	public String getInout() {
		return this.inout;
	}

	public void setInout(String inout) {
		this.inout = inout;
	}
}
