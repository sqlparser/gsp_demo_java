package demos.dlineage.dataflow.model.json;

import com.alibaba.fastjson.annotation.JSONField;

public class Relation {
	@JSONField(ordinal = 1)
	private String id;
	@JSONField(ordinal = 2)
	private String type;
	@JSONField(ordinal = 3)
	private String effectType;
	@JSONField(ordinal = 4)
	private RelationElement target;
	@JSONField(ordinal = 5)
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

}
