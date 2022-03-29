package postgresql;
/*
 * Date: 13-12-5
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExceptionClause;
import gudusoft.gsqlparser.nodes.TExceptionHandler;
import gudusoft.gsqlparser.stmt.postgresql.TNullStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_exception extends TestCase {
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
                   "-- some processing which might cause an exception\n" +
                   " null;\n" +
                   "\tEXCEPTION WHEN OTHERS THEN\n" +
                   "\t\tGET STACKED DIAGNOSTICS text_var1 = MESSAGE_TEXT,\n" +
                   "\t\ttext_var2 = PG_EXCEPTION_DETAIL,\n" +
                   "\t\ttext_var3 = PG_EXCEPTION_HINT;\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 1);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstplsql_nullstmt);
          TNullStmt nullStmt = (TNullStmt)stmt;
          assertTrue(nullStmt.toString().equalsIgnoreCase("null"));

        TExceptionClause exceptionClause = createFunction.getExceptionClause();
        assertTrue(exceptionClause.getHandlers().size() == 1);
        TExceptionHandler handler = exceptionClause.getHandlers().getExceptionHandler(0);
        assertTrue(handler.getExceptionNames().getObjectName(0).toString().equalsIgnoreCase("OTHERS"));
        assertTrue(handler.getStatements().size() == 1);
        stmt = handler.getStatements().get(0);
        //System.out.println(stmt.toString());
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstgetdiagnostics);

      }


}
