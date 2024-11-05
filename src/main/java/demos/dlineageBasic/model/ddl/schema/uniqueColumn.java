package demos.dlineageBasic.model.ddl.schema;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name="unique-column")
public class uniqueColumn {

	@Attribute(required = true)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
