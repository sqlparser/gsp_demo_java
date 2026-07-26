package demos.dlineageBasic.model.ddl.schema;

import org.simpleframework.xml.Attribute;

public class reference {
	@Attribute(required = true)
	private String foreign;
	
	@Attribute(required = true)
	private String local;

	public String getForeign() {
		return foreign;
	}

	public void setForeign(String foreign) {
		this.foreign = foreign;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
}
