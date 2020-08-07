
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.nodes.TObjectName;

public class ViewColumn extends TableColumn {

	private int columnIndex;

	public ViewColumn(View view, TObjectName columnObject, int index) {
		super(view, columnObject);
		this.columnIndex = index;
	}

	public Table getView() {
		return getTable();
	}

	public int getColumnIndex() {
		return columnIndex;
	}

}
