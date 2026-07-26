package demos.dlineageBasic.model.xml;

import org.simpleframework.xml.Attribute;

public class procedure {
	@Attribute(required = false)
	private String owner;

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String highlightInfo;

	@Attribute(required = false)
	private String coordinate;

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHighlightInfo() {
		return highlightInfo;
	}

	public void setHighlightInfo(String highlightInfo) {
		this.highlightInfo = highlightInfo;
	}

}
