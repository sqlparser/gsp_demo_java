package postgresql;
/*
 * Date: 13-12-5
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TOpenforStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_openfor extends TestCase {

    public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION reffunc(refcursor) RETURNS refcursor AS '\n" +
                   "BEGIN\n" +
                   "OPEN $1 FOR SELECT col FROM test;\n" +
                   "RETURN $1;\n" +
                   "END;\n" +
                   "' LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 2);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_openforstmt);
          TOpenforStmt openforStmt = (TOpenforStmt)stmt;
          assertTrue(openforStmt.getCursorVariableName().toString().equalsIgnoreCase("$1"));
            TSelectSqlStatement subquery = openforStmt.getSubquery();
            assertTrue(subquery.getResultColumnList().getResultColumn(0).getExpr().toString().equalsIgnoreCase("col"));
            assertTrue(subquery.tables.getTable(0).getFullName().equalsIgnoreCase("test"));

      }

}
