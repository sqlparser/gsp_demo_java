package demos.dlineage.dataflow.model.json;

import com.alibaba.fastjson.annotation.JSONField;

public class JoinRelation extends Relation {
	@JSONField(ordinal = 1)
	private String id;
	@JSONField(ordinal = 2)
	private String type;
	@JSONField(ordinal = 3)
	private String joinType;
	@JSONField(ordinal = 4)
	private String clause;
	@JSONField(ordinal = 5)
	private String condition;
	@JSONField(ordinal = 6)
	private RelationElement target;
	@JSONField(ordinal = 7)
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

	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public String getClause() {
		return clause;
	}

	public void setClause(String clause) {
		this.clause = clause;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
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
