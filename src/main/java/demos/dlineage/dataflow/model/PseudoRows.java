package demos.dlineage.dataflow.model;

public class PseudoRows<T> {

	protected int id;

	private T holder;

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
}
