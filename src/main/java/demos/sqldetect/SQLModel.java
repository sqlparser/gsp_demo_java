package demos.sqldetect;

import gudusoft.gsqlparser.TCustomSqlStatement;
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

public class SQLModel {
	private int id;
	private String spName;
	private List<QueryModel> querys = new ArrayList<QueryModel>();
	private TCustomSqlStatement model;
	private int tableMaxId;

	public int getTableMaxId() {
		return tableMaxId;
	}

	public void setTableMaxId(int tableMaxId) {
		this.tableMaxId = tableMaxId;
	}

	public List<QueryModel> getQuerys() {
		return querys;
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

	public SQLType getSqlType() {
		if (model instanceof TSelectSqlStatement)
			return SQLType.SELECT;
		else if (model instanceof TUpdateSqlStatement)
			return SQLType.UDPATE;
		else if (model instanceof TDeleteSqlStatement)
			return SQLType.DELETE;
		else if (model instanceof TDropTableSqlStatement)
			return SQLType.DROP;
		else if (model instanceof TMergeSqlStatement)
			return SQLType.MERGE;
		else if (model instanceof TCreateTableSqlStatement)
			return SQLType.CREATE;
		else if (model instanceof TTruncateStatement)
			return SQLType.TRUNCATE;
		else if (model instanceof TInsertSqlStatement)
			return SQLType.INSERT;
		else
			return SQLType.OTHER;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getSqlText() {
		return model.toString();
	}

	public TCustomSqlStatement getModel() {
		return model;
	}

	public void setModel(TCustomSqlStatement model) {
		this.model = model;
	}

	public QueryModel addQuery(TCustomSqlStatement query) {
		QueryModel queryModel = new QueryModel();
		queryModel.setSqlModel(this);
		queryModel.setModel(query);

		if (!querys.contains(queryModel)) {
			queryModel.setId(querys.size() + 1);
			querys.add(queryModel);
			return queryModel;
		} else
			return querys.get(querys.indexOf(queryModel));
	}

	public List<ColumnModel> getColumns() {
		List<ColumnModel> columns = new ArrayList<ColumnModel>();
		for (int i = 0; i < querys.size(); i++) {
			QueryModel query = querys.get(i);
			for (int j = 0; j < query.getTables().size(); j++) {
				TableModel table = query.getTables().get(j);
				for (int k = 0; k < table.getColumns().size(); k++) {
					ColumnModel column = table.getColumns().get(k);
					if (!columns.contains(column))
						columns.add(column);
				}
			}
		}
		return columns;
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
		SQLModel other = (SQLModel) obj;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		return true;
	}

}
