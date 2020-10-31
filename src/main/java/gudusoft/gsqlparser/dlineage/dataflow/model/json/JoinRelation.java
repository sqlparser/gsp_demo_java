package gudusoft.gsqlparser.dlineage.dataflow.model.json;

public class JoinRelation extends Relation {
	private String id;
	private String type;
	private String joinType;
	private String clause;
	private String condition;
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
