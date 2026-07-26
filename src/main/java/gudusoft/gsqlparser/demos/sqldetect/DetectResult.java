package demos.sqldetect;

import gudusoft.gsqlparser.TCustomSqlStatement;

import java.util.ArrayList;
import java.util.List;

public class DetectResult {
	private boolean isError = true;
	private String errorMessage = "";
	private List<SQLModel> sqls = new ArrayList<SQLModel>();

	public List<SQLModel> getSqls() {
		return sqls;
	}

	public String toString() {
		if (isError)
			return errorMessage;

		StringBuffer buffer = new StringBuffer();
		buffer.append("sql(id|startpos|endpos|sqltype|)\n");
		for (int i = 0; i < sqls.size(); i++) {
			SQLModel model = sqls.get(i);
			buffer.append(model.getId()).append("|")
					.append(model.getStartPos().x).append(",")
					.append(model.getStartPos().y).append("|")
					.append(model.getEndPos().x).append(",")
					.append(model.getEndPos().y).append("|")
					.append(model.getSqlType().toString()).append("|")
					.append("\n");
		}

		buffer.append("\nquery(id|startpos|endpos|sqlid|)\n");
		for (int i = 0; i < sqls.size(); i++) {
			SQLModel model = sqls.get(i);
			for (int j = 0; j < model.getQuerys().size(); j++) {
				QueryModel query = model.getQuerys().get(j);
				buffer.append(query.getId()).append("|")
						.append(query.getStartPos().x).append(",")
						.append(query.getStartPos().y).append("|")
						.append(query.getEndPos().x).append(",")
						.append(query.getEndPos().y).append("|")
						.append(query.getSqlModel().getId()).append("|")
						.append("\n");
			}
		}

		buffer.append("\ntable(id|tablename|tablealias|tablepos|action|queryid|)\n");
		for (int i = 0; i < sqls.size(); i++) {
			SQLModel model = sqls.get(i);
			for (int j = 0; j < model.getQuerys().size(); j++) {
				QueryModel query = model.getQuerys().get(j);
				for (int k = 0; k < query.getTables().size(); k++) {
					TableModel table = query.getTables().get(k);
					if (table.getModel().isBaseTable()) {
						buffer.append(table.getId()).append("|")
								.append(table.getTableName()).append("|")
								.append(table.getTableAlias()).append("|")
								.append(table.getTablePos().x).append(",")
								.append(table.getTablePos().y).append("|")
								.append(table.getTableAction()).append("|")
								.append(table.getQueryId()).append("|")
								.append("\n");
					}
				}
			}
		}

		buffer.append("\ncolumn(columnname|columnalias|columnpos|location|tableid|determined|)\n");
		for (int i = 0; i < sqls.size(); i++) {
			SQLModel model = sqls.get(i);
			for (int j = 0; j < model.getColumns().size(); j++) {
				ColumnModel column = model.getColumns().get(j);
				buffer.append(column.getColumnName()).append("|")
						.append(column.getColumnAlias()).append("|")
						.append(column.getColumnPos().x).append(",")
						.append(column.getColumnPos().y).append("|")
						.append(column.getLocation()).append("|")
						.append(column.getTableModel().getId()).append("|")
						.append(column.isDetermined() ? "Y" : "N").append("|")
						.append("\n");
			}
		}

		return buffer.toString();
	}

	public SQLModel addSql(TCustomSqlStatement stmt) {
		SQLModel sqlModel = new SQLModel();
		sqlModel.setModel(stmt);
		if (!sqls.contains(sqlModel)) {
			sqlModel.setId(sqls.size() + 1);
			sqls.add(sqlModel);
		}
		return sqls.get(sqls.indexOf(sqlModel));
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isError() {
		return isError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
