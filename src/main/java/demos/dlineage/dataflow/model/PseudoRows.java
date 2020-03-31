package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

public class PseudoRows<T> {

	protected int id;

	private T holder;

	private List<Relation> relations = new ArrayList<>();

	public PseudoRows(T holder) {
		this.holder = holder;
		id = ++ModelBindingManager.get().TABLE_COLUMN_ID;
	}

	public T getHolder() {
		return holder;
	}

	public String getName() {
		return "PseudoRows";
	}

	public int getId() {
		return id;
	}

	public void holdRelation(Relation relation) {
		if (relation != null) {
			relations.add(relation);
		}
	}
	
	public List<Relation> getHoldRelations(){
		return relations;
	}
}
