package demos.dlineage.dataflow.model;

public class PseduoRows<T> {

	protected int id;

	private T holder;

	public PseduoRows(T holder) {
		this.holder = holder;
		id = ++ModelBindingManager.get().TABLE_COLUMN_ID;
	}

	public T getHolder() {
		return holder;
	}

	public String getName() {
		return "pseduoRows";
	}

	public int getId() {
		return id;
	}
}
