package demos.sqldetect;

import gudusoft.gsqlparser.nodes.TCTE;

public class CTEDetect {

	public void detectCTE(QueryModel queryModel, TCTE cte) {
		if (cte.getSubquery() != null) {
			queryModel.getSqlDetect().detectSubQuery(queryModel,
					cte.getSubquery());
		}
	}

}
