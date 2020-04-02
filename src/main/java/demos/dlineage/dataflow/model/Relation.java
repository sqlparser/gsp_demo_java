
package demos.dlineage.dataflow.model;

public interface Relation {

	RelationElement<?> getTarget();

	RelationElement<?>[] getSources();

	RelationType getRelationType();

	EffectType getEffectType();

	String getFunction();
	
	boolean isShowStarRelation();
}
