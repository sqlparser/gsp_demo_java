package demos.dlineage.dataflow.model.xml;

import demos.dlineage.util.Pair;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class procedure {
	@Attribute(required = false)
	private String id;
	@Attribute(required = false)
	private String name;
	@Attribute(required = false)
	private String type;
	@Attribute(required = false)
	private String coordinate;
	@ElementList(entry = "argument", inline = true, required = false)
	private List<argument> arguments;

	public procedure() {
	}

	public String getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOccurrencesNumber() {
		return PositionUtil.getOccurrencesNumber(this.coordinate);
	}

	public Pair<Integer, Integer> getStartPos(int index) {
		return PositionUtil.getStartPos(this.coordinate, index);
	}

	public Pair<Integer, Integer> getEndPos(int index) {
		return PositionUtil.getEndPos(this.coordinate, index);
	}

	public List<argument> getArguments() {
		return this.arguments;
	}

	public void setArguments(List<argument> arguments) {
		this.arguments = arguments;
	}
}
