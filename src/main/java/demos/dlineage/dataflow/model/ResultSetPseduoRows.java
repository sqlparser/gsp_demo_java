package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;

public class ResultSetPseduoRows extends PseduoRows<ResultSet> {

	public ResultSetPseduoRows(ResultSet holder) {
		super(holder);
	}

	public Pair<Long, Long> getStartPosition() {
		return getHolder().getStartPosition();
	}

	public Pair<Long, Long> getEndPosition() {
		return getHolder().getEndPosition();
	}
}
