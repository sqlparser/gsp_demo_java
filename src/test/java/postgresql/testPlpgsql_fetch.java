package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TFetchStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_fetch extends TestCase {

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
                 "FETCH curs1 INTO rowvar;\n" +
                 "FETCH curs2 INTO foo, bar, baz;\n" +
                 "FETCH LAST FROM curs3 INTO x, y;\n" +
                 "FETCH RELATIVE -2 FROM curs4 INTO x;\n" +
                 "\n" +
                 "\n" +
                 "END;\n" +
                 "$$ LANGUAGE plpgsql;";
         assertTrue(sqlparser.parse() == 0);

         TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 4);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_fetchstmt);
        TFetchStmt fetchStmt = (TFetchStmt)stmt;
        assertTrue(fetchStmt.getCursorName().toString().equalsIgnoreCase("curs1"));
       assertTrue(fetchStmt.getVariableNames().getExpression(0).toString().equalsIgnoreCase("rowvar"));

    }

}
