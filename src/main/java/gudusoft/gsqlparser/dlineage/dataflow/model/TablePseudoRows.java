package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.dlineage.util.Pair3;

public class TablePseudoRows extends PseudoRows<Table> {

	public TablePseudoRows(Table holder) {
		super(holder);
	}

	public Pair3<Long, Long, String> getStartPosition() {
		return getHolder().getStartPosition();
	}

	public Pair3<Long, Long, String> getEndPosition() {
		return getHolder().getEndPosition();
	}
}
