package gudusoft.gsqlparser.dlineage.dataflow.model.xml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "type", "function", "effectType", "clause", "joinType", "target", "sources", "condition" })
public class relation implements Cloneable {

	private String id;

	private String type;

	private String function;

	private String effectType;

	private String clause;

	private String joinType;

	private targetColumn target;

	private List<sourceColumn> sources;

	private String condition;

	@XmlAttribute(required = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(required = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "target", required = false)
	public targetColumn getTarget() {
		return target;
	}

	public void setTarget(targetColumn target) {
		this.target = target;
	}

	@XmlElement(name = "source", required = false)
	public List<sourceColumn> getSources() {
		if (this.sources == null) {
			this.sources = new LinkedList<sourceColumn>();
		}
		return sources;
	}

	public void setSources(List<sourceColumn> sources) {
		this.sources = sources;
	}

	@XmlAttribute(required = false)
	public String getClause() {
		return clause;
	}

	public void setClause(String clause) {
		this.clause = clause;
	}

	@XmlAttribute(required = false)
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@XmlAttribute(required = false)
	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public boolean isDataFlow() {
		return "fdd".equals(type);
	}

	@XmlAttribute(required = false)
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@XmlAttribute(required = false)
	public String getEffectType() {
		return effectType;
	}

	public void setEffectType(String effectType) {
		this.effectType = effectType;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
