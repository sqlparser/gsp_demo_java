package demos.columnInWhereClause;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;

public class ColumnInWhereClause {

	public static void main(String[] args) {
		long t = System.currentTimeMillis();

		if (args.length < 1){
			System.out.println("Usage: java ColumnInWhereClause [/f <path_to_sql_file>]  [/t <database type>]");
			return;
		}

		String sqlFilename = null;

		List<String> argList = Arrays.asList(args);

		if ( argList.indexOf( "/f" ) != -1
				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
		{
			sqlFilename = args[argList.indexOf( "/f" ) + 1];
		}
		else
		{
			System.out.println( "Please specify a sql file ." );
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		TGSqlParser sqlparser = new TGSqlParser(vendor);
		sqlparser.sqlfilename  = sqlFilename;

		System.out.println("Selected SQL dialect: "+vendor.toString());

		int ret = sqlparser.parse();
		if (ret != 0){
			System.out.println(sqlFilename+ System.getProperty("line.separator")+sqlparser.getErrormessage());
			System.out.println("");
		}else {
			for(int i=0;i<sqlparser.sqlstatements.size();i++){
				// sqlparser.sqlstatements.get(i).acceptChildren( new WhereVisitor());
				sqlparser.sqlstatements.get(i).acceptChildren( new ColumnVisitor("name"));
			}
		}

		System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) +",file processed: "+ sqlFilename);
		// printColumnInWhere();
	}

	private static void printColumnInWhere(){
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
