
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TObjectName;

public class View extends Table {

	private TCustomSqlStatement viewObject;

	public View(TCustomSqlStatement view, TObjectName viewName) {
		super(viewName);
		this.viewObject = view;
	}

	public TCustomSqlStatement getViewObject() {
		return viewObject;
	}
}
