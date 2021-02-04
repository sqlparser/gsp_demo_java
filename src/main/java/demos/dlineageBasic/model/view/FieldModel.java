package demos.dlineageBasic.model.view;

import java.awt.Point;


public class FieldModel {
	private String name;
	private String schema;
	private String highlightInfo;
	private String coordinate;

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getHighlightInfo() {
		return highlightInfo;
	}

	public void setHighlightInfo(String highlightInfo) {
		this.highlightInfo = highlightInfo;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	private Point location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public String getFullName() {
		if (schema != null)
			return schema + "." + name;
		else
			return name;
	}

}
