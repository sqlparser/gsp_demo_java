package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TOpenStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_open extends TestCase {

    public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION scan_rows(int[]) RETURNS void AS $$\n" +
                   "DECLARE\n" +
                   "\tcurs1 refcursor;\n" +
                   "\tcurs2 CURSOR FOR SELECT\n" +
                   "\t*\n" +
                   "\tFROM tenk1;\n" +
                   "\tcurs3 CURSOR (key integer) FOR SELECT\n" +
                   "\t*\n" +
                   "\tFROM tenk1 WHERE unique1 = key;\n" +
                   "BEGIN\n" +
                   "\tOPEN curs2;\n" +
                   "\tOPEN curs3(42);\n" +
                   "\tOPEN curs3(key := 42);\n" +
                   "\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 3);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_openstmt);
          TOpenStmt openStmt = (TOpenStmt)stmt;
          assertTrue(openStmt.getCursorName().toString().equalsIgnoreCase("curs2"));

        stmt = createFunction.getBodyStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_openstmt);
        openStmt = (TOpenStmt)stmt;
        assertTrue(openStmt.getCursorName().toString().equalsIgnoreCase("curs3"));
        assertTrue(openStmt.getCursorParameterNames().getExpression(0).toString().equalsIgnoreCase("42"));
      }


}
