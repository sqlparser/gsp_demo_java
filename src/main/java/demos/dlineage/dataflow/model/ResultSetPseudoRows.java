package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair3;

public class ResultSetPseudoRows extends PseudoRows<ResultSet> {

	public ResultSetPseudoRows(ResultSet holder) {
		super(holder);
	}

	public Pair3<Long, Long, String> getStartPosition() {
		return getHolder().getStartPosition();
	}

	public Pair3<Long, Long, String> getEndPosition() {
		return getHolder().getEndPosition();
	}
}
