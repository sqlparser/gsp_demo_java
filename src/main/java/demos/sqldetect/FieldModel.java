package demos.sqldetect;

import gudusoft.gsqlparser.nodes.TResultColumn;

public class FieldModel {
	private ColumnModel refColumn;
	private TResultColumn model;

	public ColumnModel getRefColumn() {
		return refColumn;
	}

	public void setRefColumn(ColumnModel refColumn) {
		this.refColumn = refColumn;
	}

	public TResultColumn getModel() {
		return model;
	}

	public void setModel(TResultColumn model) {
		this.model = model;
	}

	public String getAlias() {
		if (model.getAliasClause() != null
				&& model.getAliasClause().getAliasName() != null) {
			return model.getAliasClause().getAliasName().toString();
		}
		return "";
	}

}
