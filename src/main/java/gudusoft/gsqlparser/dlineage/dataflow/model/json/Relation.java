package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class Relation {
	private String id;
	private String type;
	private String function;
	private String effectType;
	private RelationElement target;
	private RelationElement[] sources;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEffectType() {
		return effectType;
	}

	public void setEffectType(String effectType) {
		this.effectType = effectType;
	}

	public RelationElement getTarget() {
		return target;
	}

	public void setTarget(RelationElement target) {
		this.target = target;
	}

	public RelationElement[] getSources() {
		return sources;
	}

	public void setSources(RelationElement[] sources) {
		this.sources = sources;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
}
