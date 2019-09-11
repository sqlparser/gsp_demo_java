package demos.sqldetect;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class QueryModel {
	private int id;
	private SQLModel sqlModel;
	private List<TableModel> tables = new ArrayList<TableModel>();
	private TCustomSqlStatement model;
	private SQLDetect sqlDetect;

	public SQLDetect getSqlDetect() {
		return sqlDetect;
	}

	public void setSqlDetect(SQLDetect sqlDetect) {
		this.sqlDetect = sqlDetect;
	}

	public TCustomSqlStatement getModel() {
		return model;
	}

	public void setModel(TCustomSqlStatement model) {
		this.model = model;
	}

	public List<TableModel> getTables() {
		return tables;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Point getStartPos() {
		return new Point((int) model.getStartToken().lineNo,
				(int) model.getStartToken().columnNo);
	}

	public Point getEndPos() {
		return new Point(
				(int) model.getEndToken().lineNo,
				(int) (model.getEndToken().columnNo + model.getEndToken().astext
						.length()));
	}

	public SQLModel getSqlModel() {
		return sqlModel;
	}

	public void setSqlModel(SQLModel sqlModel) {
		this.sqlModel = sqlModel;
	}

	public TableModel addTable(TTable table) {
		TableModel tableModel = new TableModel();
		tableModel.setQueryModel(this);
		tableModel.setModel(table);
		if (!tables.contains(tableModel)) {
			getSqlModel().setTableMaxId(getSqlModel().getTableMaxId() + 1);
			tableModel.setId(getSqlModel().getTableMaxId());
			tables.add(tableModel);
			return tableModel;
		} else {
			return tables.get(tables.indexOf(tableModel));
		}
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
		QueryModel other = (QueryModel) obj;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		return true;
	}

	public ColumnModel getColumnModelByName(TObjectName columnName) {
		List<ColumnModel> columns = sqlModel.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			ColumnModel column = columns.get(i);
			if (column.getModel() == columnName)
				return column;
		}
		return null;
	}
}
