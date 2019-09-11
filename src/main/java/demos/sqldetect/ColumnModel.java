package demos.sqldetect;

import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.nodes.TObjectName;

import java.awt.Point;

public class ColumnModel {
	private TableModel tableModel;
	private TObjectName model;
	private FieldModel refResultColumn;
	private boolean isWrote = false;

	public boolean isWrote() {
		return isWrote;
	}

	public void setWrote(boolean isWrote) {
		this.isWrote = isWrote;
	}

	public FieldModel getRefResultColumn() {
		return refResultColumn;
	}

	public void setRefResultColumn(FieldModel refResultColumn) {
		this.refResultColumn = refResultColumn;
	}

	public TObjectName getModel() {
		return model;
	}

	public void setModel(TObjectName model) {
		this.model = model;
	}

	public String getColumnName() {
		return model.getColumnNameOnly();
	}

	public String getColumnAlias() {
		if (refResultColumn != null)
			return refResultColumn.getAlias();
		return "";
	}

	public Point getColumnPos() {
		return new Point((int) model.getStartToken().lineNo,
				(int) model.getStartToken().columnNo);
	}

	public ColumnLocation getLocation() {
		ESqlClause clause = model.getLocation();
		switch (clause) {
		case set:
			if (isWrote)
				return ColumnLocation.LEFTOFSETCLAUSE;
			else
				return ColumnLocation.RIGHTOFSETCLAUSE;
		case joinCondition:
			return ColumnLocation.JOINCONDITION;
		case selectList:
			return ColumnLocation.SELECTLIST;
		case groupby:
			return ColumnLocation.GROUPBY;
		case orderby:
			return ColumnLocation.ORDERY;
		case where:
			return ColumnLocation.WHERE;
		default:
			break;
		}
		return ColumnLocation.OTHER;
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public boolean isDetermined() {
		return model.isTableDetermined();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnModel other = (ColumnModel) obj;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		return true;
	}

}
