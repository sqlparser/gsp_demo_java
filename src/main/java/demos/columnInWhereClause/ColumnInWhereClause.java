package demos.columnInWhereClause;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;

public class ColumnInWhereClause {

	public static void main(String[] args) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
		sqlparser.sqltext = "Select firstname, lastname, age from Clients where State = \"CA\" and  City = \"Hollywood\"";
		int i = sqlparser.parse();
		if (i == 0) {
			List<TCustomSqlStatement> stmts = new ArrayList<TCustomSqlStatement>();
			traverseStmts(sqlparser.sqlstatements, stmts);
			for (int j = 0; j < stmts.size(); j++) {
				WhereCondition w = new WhereCondition(stmts.get(j).getWhereClause().getCondition());
				w.printColumn();
			}
		} else
			System.out.println(sqlparser.getErrormessage());
	}

	private static void traverseStmts(TStatementList sqlstatements, List<TCustomSqlStatement> stmts) {
		for (int i = 0; i < sqlstatements.size(); i++) {
			TCustomSqlStatement stmt = sqlstatements.get(i);
			if (stmt.getStatements() != null) {
				traverseStmts(stmt.getStatements(), stmts);
			}
			if (!stmts.contains(stmt) && stmt.getWhereClause() != null) {
				stmts.add(stmt);
			}
		}
	}
}
