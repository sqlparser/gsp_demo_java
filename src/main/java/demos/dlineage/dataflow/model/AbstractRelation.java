
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRelation implements Relation {

    private int id;

    private EffectType effectType;

    protected RelationElement<?> target;
    protected List<RelationElement<?>> sources = new ArrayList<RelationElement<?>>();

    public AbstractRelation() {
        id = ++ModelBindingManager.get().RELATION_ID;
    }

    public int getId() {
        return id;
    }

    public RelationElement<?> getTarget() {
        return target;
    }

    public void setTarget(RelationElement<?> target) {
        this.target = target;
    }

    public RelationElement<?>[] getSources() {
        return sources.toArray(new RelationElement<?>[0]);
    }

    public void addSource(RelationElement<?> source) {
        if (source != null && !sources.contains(source)) {
            sources.add(source);
        }
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
    }

}
