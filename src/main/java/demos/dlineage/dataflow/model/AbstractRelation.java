
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRelation implements Relation {

    private int id;

    private String function; 
    
    private EffectType effectType;
    
    private boolean showStarRelation;

    protected RelationElement<?> target;
    
    protected List<RelationElement<?>> sources = new ArrayList<RelationElement<?>>();

    public AbstractRelation() {
        id = ++ModelBindingManager.get().RELATION_ID;
		if (id == 4099 || id == 4122) {
			System.out.println("???");
		}
    }

    public int getId() {
        return id;
    }

    public RelationElement<?> getTarget() {
        return target;
    }

    public void setTarget(RelationElement<?> target) {
        this.target = target;
        if(target.getElement() instanceof PseudoRows<?>){
        	((PseudoRows<?>)target.getElement()).holdRelation(this);
        }
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

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public boolean isShowStarRelation() {
		return showStarRelation;
	}

	public void setShowStarRelation(boolean showStarRelation) {
		this.showStarRelation = showStarRelation;
	}
	
}
