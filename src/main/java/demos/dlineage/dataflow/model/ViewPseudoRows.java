package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;

public class ViewPseudoRows extends PseudoRows<View> {

	public ViewPseudoRows(View holder) {
		super(holder);
	}

	public Pair<Long, Long> getStartPosition() {
		return getHolder().getStartPosition();
	}

	public Pair<Long, Long> getEndPosition() {
		return getHolder().getEndPosition();
	}
}
