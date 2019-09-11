package demos.sqldetect;

import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TTruncateStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TableModel {
	private int id;
	private QueryModel queryModel;
	private List<ColumnModel> columns = new ArrayList<ColumnModel>();
	private TTable model;

	public TTable getModel() {
		return model;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableName() {
		return model.getFullName();
	}

	public String getTableAlias() {
		if (model.getAliasClause() != null
				&& model.getAliasClause().getAliasName() != null)
			return model.getAliasClause().getAliasName().toString();
		return "";
	}

	public Point getTablePos() {
		return new Point((int) model.getStartToken().lineNo,
				(int) model.getStartToken().columnNo);
	}

	public TableAction getTableAction() {
		if (getQueryModel().getModel() instanceof TSelectSqlStatement)
			return TableAction.SELECT;
		else if (getQueryModel().getModel() instanceof TUpdateSqlStatement)
			return TableAction.UDPATE;
		else if (getQueryModel().getModel() instanceof TDeleteSqlStatement)
			return TableAction.DELETE;
		else if (getQueryModel().getModel() instanceof TDropTableSqlStatement)
			return TableAction.DROP;
		else if (getQueryModel().getModel() instanceof TMergeSqlStatement)
			return TableAction.MERGE;
		else if (getQueryModel().getModel() instanceof TCreateTableSqlStatement)
			return TableAction.CREATE;
		else if (getQueryModel().getModel() instanceof TTruncateStatement)
			return TableAction.TRUNCATE;
		else if (getQueryModel().getModel() instanceof TInsertSqlStatement)
			return TableAction.INSERT;
		else
			return TableAction.OTHER;
	}

	public QueryModel getQueryModel() {
		return queryModel;
	}

	public void setQueryModel(QueryModel queryModel) {
		this.queryModel = queryModel;
	}

	public void setModel(TTable model) {
		this.model = model;
	}

	public int getQueryId() {
		return getQueryModel().getId();
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
		TableModel other = (TableModel) obj;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		return true;
	}

	public ColumnModel addColumn(TObjectName objectName) {
		ColumnModel columnModel = new ColumnModel();
		columnModel.setTableModel(this);
		columnModel.setModel(objectName);
		if (!columns.contains(columnModel)) {
			columns.add(columnModel);
			return columnModel;
		} else {
			return columns.get(columns.indexOf(columnModel));
		}

	}

}
