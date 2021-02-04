package demos.dlineageBasic.model.view;

import java.awt.Point;

public class AliasModel {
	private String name;
	private Point location;
	private String highlightInfo;
	private FieldModel field;
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

	public FieldModel getField() {
		return field;
	}

	public void setField(FieldModel field) {
		this.field = field;
	}

}
