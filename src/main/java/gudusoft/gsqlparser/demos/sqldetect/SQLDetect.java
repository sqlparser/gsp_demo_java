package demos.sqldetect;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TInsertCondition;
import gudusoft.gsqlparser.nodes.TInsertIntoValue;
import gudusoft.gsqlparser.nodes.TMergeInsertClause;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class SQLDetect {

	public DetectResult detect(EDbVendor dbvendor, File sqlFile) {
		TGSqlParser parser = new TGSqlParser(dbvendor);
		parser.sqlfilename = sqlFile.getAbsolutePath();
		int resultCode = parser.parse();
		DetectResult result = new DetectResult();
		if (resultCode != 0) {
			result.setError(true);
			result.setErrorMessage(parser.getErrormessage());
			return result;
		}
		result.setError(false);
		for (int k = 0; k < parser.sqlstatements.size(); k++) {
			SQLModel sqlModel = result.addSql(parser.getSqlstatements().get(k));
			detectSQL(sqlModel);
		}
		return result;
	}

	public DetectResult detect(EDbVendor dbvendor, String sqlText) {
		TGSqlParser parser = new TGSqlParser(dbvendor);
		parser.sqltext = sqlText;
		int resultCode = parser.parse();
		DetectResult result = new DetectResult();
		if (resultCode != 0) {
			result.setError(true);
			result.setErrorMessage(parser.getErrormessage());
			return result;
		}
		result.setError(false);
		for (int k = 0; k < parser.sqlstatements.size(); k++) {
			SQLModel sqlModel = result.addSql(parser.getSqlstatements().get(k));
			detectSQL(sqlModel);
		}
		return result;
	}

	private void detectSQL(SQLModel sqlModel) {
		QueryModel queryModel = sqlModel.addQuery(sqlModel.getModel());
		queryModel.setSqlDetect(this);
		detectQuery(queryModel);
	}

	public void detectQuery(QueryModel queryModel) {
		if (queryModel.getModel() instanceof TSelectSqlStatement) {
			TSelectSqlStatement stmt = (TSelectSqlStatement) queryModel
					.getModel();
			if (stmt.getSetOperator() != TSelectSqlStatement.SET_OPERATOR_NONE) {
				detectSubQuery(queryModel, stmt.getLeftStmt());
				detectSubQuery(queryModel, stmt.getRightStmt());
			} else {
				detectSelectQuery(queryModel);
			}
		} else if (queryModel.getModel() instanceof TInsertSqlStatement) {
			if (((TInsertSqlStatement) queryModel.getModel()).getSubQuery() != null) {
				detectSubQuery(queryModel,
						((TInsertSqlStatement) queryModel.getModel())
								.getSubQuery());
			}
			detectInsertQuery(queryModel);
		} else if (queryModel.getModel() instanceof TMergeSqlStatement) {
			detectMergeQuery(queryModel);
		} else if (queryModel.getModel() instanceof TUpdateSqlStatement) {
			detectUpdateQuery(queryModel);
		} else if (queryModel.getModel() instanceof TDeleteSqlStatement) {
			detectDeleteQuery(queryModel);
		} else if (queryModel.getModel().getStatements() != null) {
			for (int i = 0; i < queryModel.getModel().getStatements().size(); i++) {
				detectSubQuery(queryModel, queryModel.getModel()
						.getStatements().get(i));
			}
		}
	}

	private void detectCustomQuery(QueryModel queryModel) {
		TCustomSqlStatement customStmt = (TCustomSqlStatement) queryModel
				.getModel();
		for (int i = 0; i < customStmt.tables.size(); i++) {
			TTable table = customStmt.tables.getTable(i);
			TableModel tableModel = queryModel.addTable(table);
			for (int j = 0; j < table.getLinkedColumns().size(); j++) {
				tableModel.addColumn(table.getLinkedColumns()
						.getObjectName(j));
			}
			if (table.getSubquery() != null) {
				detectSubQuery(queryModel, table.getSubquery());
			}
		}

		if (customStmt.getWhereClause() != null) {
			new ClauseDetect().detectWhereClause(queryModel,
					customStmt.getWhereClause());
		}

		if (customStmt.getResultColumnList() != null) {
			detectResultColumns(queryModel, customStmt.getResultColumnList());
		}
	}

	public void detectResultColumns(QueryModel queryModel,
			TResultColumnList columns) {
		for (int i = 0; i < columns.size(); i++) {
			TResultColumn column = columns.getResultColumn(i);
			new ColumnDetect().detect(queryModel, column);
		}
	}

	private void detectDeleteQuery(QueryModel queryModel) {
		detectCustomQuery(queryModel);
	}

	private void detectUpdateQuery(QueryModel queryModel) {
		detectCustomQuery(queryModel);
		TUpdateSqlStatement updateStmt = (TUpdateSqlStatement) queryModel
				.getModel();

		if (updateStmt.getCteList() != null) {
			for (int i = 0; i < updateStmt.getCteList().size(); i++) {
				TCTE cte = updateStmt.getCteList().getCTE(i);
				new CTEDetect().detectCTE(queryModel, cte);
			}
		}

		if (updateStmt.getOrderByClause() != null) {
			new ClauseDetect().detectOrderByClause(queryModel,
					updateStmt.getOrderByClause());
		}

		if (updateStmt.getLimitClause() != null) {
			new ClauseDetect().detectLimitClause(queryModel,
					updateStmt.getLimitClause());
		}
	}

	private void detectMergeQuery(QueryModel queryModel) {
		detectCustomQuery(queryModel);
		TMergeSqlStatement mergeStmt = (TMergeSqlStatement) queryModel
				.getModel();

		if (mergeStmt.getCteList() != null) {
			for (int i = 0; i < mergeStmt.getCteList().size(); i++) {
				TCTE cte = mergeStmt.getCteList().getCTE(i);
				new CTEDetect().detectCTE(queryModel, cte);
			}
		}

		if (mergeStmt.getInsertClause() != null) {
			TMergeInsertClause insertClause = mergeStmt.getInsertClause();
			if (insertClause.getColumnList() != null) {
				detectColumnLists(queryModel, insertClause.getColumnList());
			}
			if (insertClause.getInsertWhereClause() != null) {
				new ExpressionDetect().detect(queryModel,
						insertClause.getInsertWhereClause());
			}
			if (insertClause.getValuelist() != null) {
				detectResultColumns(queryModel, insertClause.getValuelist());
			}
		}
		if (mergeStmt.getCondition() != null) {
			new ExpressionDetect().detect(queryModel, mergeStmt.getCondition());
		}
		if (mergeStmt.getColumnList() != null) {
			detectColumnLists(queryModel, mergeStmt.getColumnList());
		}
		if (mergeStmt.getMatchedSearchCondition() != null) {
			new ExpressionDetect().detect(queryModel,
					mergeStmt.getMatchedSearchCondition());
		}
		if (mergeStmt.getNotMatchedSearchCondition() != null) {
			new ExpressionDetect().detect(queryModel,
					mergeStmt.getNotMatchedSearchCondition());
		}
		if (mergeStmt.getUpdateClause() != null) {
			new ClauseDetect().detectMergeUpdateClause(queryModel,
					mergeStmt.getUpdateClause());
		}
		if (mergeStmt.getWhenClauses() != null) {
			for (int i = 0; i < mergeStmt.getWhenClauses().size(); i++) {
				new ClauseDetect().detectMergeWhenClause(queryModel, mergeStmt
						.getWhenClauses().getElement(i));
			}
		}
	}

	public void detectColumnLists(QueryModel queryModel,
			TObjectNameList columnList) {

	}

	private void detectInsertQuery(QueryModel queryModel) {
		detectCustomQuery(queryModel);
		TInsertSqlStatement insertStmt = (TInsertSqlStatement) queryModel
				.getModel();

		if (insertStmt.getCteList() != null) {
			for (int i = 0; i < insertStmt.getCteList().size(); i++) {
				TCTE cte = insertStmt.getCteList().getCTE(i);
				new CTEDetect().detectCTE(queryModel, cte);
			}
		}

		if (insertStmt.getColumnList() != null) {
			detectColumnLists(queryModel, insertStmt.getColumnList());
		}

		if (insertStmt.getInsertConditions() != null) {
			for (int i = 0; i < insertStmt.getInsertConditions().size(); i++) {
				TInsertCondition condition = insertStmt.getInsertConditions()
						.getElement(i);
				if (condition.getCondition() != null) {
					new ExpressionDetect().detect(queryModel,
							condition.getCondition());
				}
				if (condition.getInsertIntoValues() != null) {
					for (int j = 0; j < condition.getInsertIntoValues().size(); j++) {
						TInsertIntoValue value = condition
								.getInsertIntoValues().getElement(j);
						detectInsertIntoValue(queryModel, value);
					}
				}
			}
		}
		if (insertStmt.getInsertIntoValues() != null) {
			for (int i = 0; i < insertStmt.getInsertIntoValues().size(); i++) {
				TInsertIntoValue value = insertStmt.getInsertIntoValues()
						.getElement(i);
				detectInsertIntoValue(queryModel, value);
			}
		}
		if (insertStmt.getFunctionCall() != null) {
			new FunctionDetect().detectFunction(queryModel,
					insertStmt.getFunctionCall());
		}
		if (insertStmt.getSetColumnValues() != null) {
			detectResultColumns(queryModel, insertStmt.getSetColumnValues());
		}
		if (insertStmt.getSubQuery() != null) {
			detectSubQuery(queryModel, insertStmt.getSubQuery());
		}
		if (insertStmt.getValues() != null) {
			detectMultiTargetList(queryModel, insertStmt.getValues());
		}
	}

	private void detectInsertIntoValue(QueryModel queryModel,
			TInsertIntoValue value) {
		if (value.getColumnList() != null) {
			detectColumnLists(queryModel, value.getColumnList());
		}
		if (value.getTargetList() != null) {
			TMultiTargetList multiTargetList = value.getTargetList();
			detectMultiTargetList(queryModel, multiTargetList);
		}
	}

	private void detectMultiTargetList(QueryModel queryModel,
			TMultiTargetList multiTargetList) {
		for (int j = 0; j < multiTargetList.size(); j++) {
			TMultiTarget target = multiTargetList.getMultiTarget(j);
			if (target.getColumnList() != null) {
				detectResultColumns(queryModel, target.getColumnList());
			}
			if (target.getSubQuery() != null) {
				detectSubQuery(queryModel, target.getSubQuery());
			}
		}
	}

	private void detectSelectQuery(QueryModel queryModel) {
		detectCustomQuery(queryModel);
		TSelectSqlStatement selectStmt = (TSelectSqlStatement) queryModel
				.getModel();

		if (selectStmt.getCteList() != null) {
			for (int i = 0; i < selectStmt.getCteList().size(); i++) {
				TCTE cte = selectStmt.getCteList().getCTE(i);
				new CTEDetect().detectCTE(queryModel, cte);
			}
		}

		if (selectStmt.getLeftStmt() != null) {
			detectSubQuery(queryModel, selectStmt.getLeftStmt());
		}
		if (selectStmt.getRightStmt() != null) {
			detectSubQuery(queryModel, selectStmt.getRightStmt());
		}

		if (selectStmt.getOrderbyClause() != null) {
			new ClauseDetect().detectOrderByClause(queryModel,
					selectStmt.getOrderbyClause());
		}
		if (selectStmt.getGroupByClause() != null) {
			new ClauseDetect().detectGroupByClause(queryModel,
					selectStmt.getGroupByClause());
		}
		if (selectStmt.getIntoClause() != null) {
			new ClauseDetect().detectIntoClause(queryModel,
					selectStmt.getIntoClause());
		}
		if (selectStmt.getLimitClause() != null) {
			new ClauseDetect().detectLimitClause(queryModel,
					selectStmt.getLimitClause());
		}

		if (selectStmt.getForUpdateClause() != null) {
			new ClauseDetect().detectForUpdateClause(queryModel,
					selectStmt.getForUpdateClause());
		}
	}

	public void detectSubQuery(QueryModel queryModel,
			TCustomSqlStatement subQuery) {
		QueryModel subQueryModel = queryModel.getSqlModel().addQuery(subQuery);
		subQueryModel.setSqlDetect(this);
		detectQuery(subQueryModel);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out
					.println("Usage: java SQLDetect scriptfile [/o <output file path>] [/t <database type>]");
			System.out
					.println("/o: Option, write the output stream to the specified file.");
			System.out
					.println("/t: Option, set the database type. Support oracle, mysql, mssql, netezza and db2, the default type is oracle");
			return;
		}

		List<String> argList = Arrays.asList(args);
		int index = argList.indexOf("/o");

		String outputFile = null;
		if (index != -1 && args.length > index + 1) {
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;
		if (outputFile != null) {
			try {
				writer = new FileOutputStream(outputFile);
				System.setOut(new PrintStream(writer));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		index = argList.indexOf("/t");

		if (index != -1 && args.length > index + 1) {
			if (args[index + 1].equalsIgnoreCase("mssql")) {
				vendor = EDbVendor.dbvmssql;
			} else if (args[index + 1].equalsIgnoreCase("db2")) {
				vendor = EDbVendor.dbvdb2;
			} else if (args[index + 1].equalsIgnoreCase("mysql")) {
				vendor = EDbVendor.dbvmysql;
			} else if (args[index + 1].equalsIgnoreCase("mssql")) {
				vendor = EDbVendor.dbvmssql;
			} else if (args[index + 1].equalsIgnoreCase("netezza")) {
				vendor = EDbVendor.dbvnetezza;
			}
		}

		DetectResult result = new SQLDetect().detect(vendor, new File(args[0]));
		System.out.println(result.toString());

		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
