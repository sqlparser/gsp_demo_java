package demos.dlineageBasic.model.ddl.schema;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="index-column")
public class indexColumn {

	@Attribute(required = true)
	private String name;
	
	@Attribute(required = false)
	private String size;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
