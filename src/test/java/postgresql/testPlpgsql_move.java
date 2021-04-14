package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.postgresql.TMoveStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_move extends TestCase {

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
                 "MOVE curs1;\n" +
                 "MOVE LAST FROM curs3;\n" +
                 "MOVE RELATIVE -2 FROM curs4;\n" +
                 "MOVE FORWARD 2 FROM curs4;\n" +
                 "\n" +
                 "\n" +
                 "END;\n" +
                 "$$ LANGUAGE plpgsql;";
         assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 4);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstpostgresqlMove);
        TMoveStmt moveStmt = (TMoveStmt)stmt;
        assertTrue(moveStmt.getCursorName().toString().equalsIgnoreCase("curs1"));

    }
}
