package test.postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TLoopStmt;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlCreateFunction;
import junit.framework.TestCase;

public class testPlpgsql_forLoop extends TestCase {

    public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION myfunc(refcursor, refcursor) RETURNS SETOF refcursor AS $$\n" +
                   "BEGIN\n" +
                   "\tFOR recordvar IN bound_cursorvar LOOP\n" +
                   "\t\tnull;\n" +
                   "\tEND LOOP;\n" +
                   "\t\n" +
                   "\tFOR recordvar IN bound_cursorvar(1,s) LOOP\n" +
                   "\t\tnull;\n" +
                   "\tEND LOOP;\n" +
                   "\t\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 2);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
          TLoopStmt loopStmt = (TLoopStmt)stmt;
          assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getIndexName().toString().equalsIgnoreCase("recordvar"));
        assertTrue(loopStmt.getCursorName().toString().equalsIgnoreCase("bound_cursorvar"));
        assertTrue(loopStmt.getBodyStatements().size() == 1);
        assertTrue(loopStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstplsql_nullstmt);

          stmt = createFunction.getBodyStatements().get(1);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
          loopStmt = (TLoopStmt)stmt;
          assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getIndexName().toString().equalsIgnoreCase("recordvar"));
        assertTrue(loopStmt.getCursorName().toString().equalsIgnoreCase("bound_cursorvar"));
        assertTrue(loopStmt.getBodyStatements().size() == 1);
        assertTrue(loopStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstplsql_nullstmt);
        assertTrue(loopStmt.getCursorParameterNames().getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(loopStmt.getCursorParameterNames().getExpression(1).toString().equalsIgnoreCase("s"));
      }

    public void testExecute(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION cs_refresh_mviews() RETURNS integer AS $$\n" +
                   "DECLARE\n" +
                   "mviews RECORD;\n" +
                   "BEGIN\n" +
                   "\t\n" +
                   " <<label1>>\n" +
                   "FOR target1 IN EXECUTE a+b  USING 1,2,3 LOOP\n" +
                   "\tnull;\n" +
                   "END LOOP label1;\n" +
                   "\n" +
                   "\tRETURN 1;\n" +
                   "\t\n" +
                   "END;\n" +
                   "$$ LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 2);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
          TLoopStmt loopStmt = (TLoopStmt)stmt;

          assertTrue(loopStmt.getLabelName().toString().equalsIgnoreCase("label1"));
          assertTrue(loopStmt.getEndlabelName().toString().equalsIgnoreCase("label1"));
          assertTrue(loopStmt.getKind() == TLoopStmt.cursor_for_loop);
        assertTrue(loopStmt.getIndexName().toString().equalsIgnoreCase("target1"));
        assertTrue(loopStmt.getExecuteExpr().toString().equalsIgnoreCase("a+b"));
        assertTrue(loopStmt.getExecuteUsingVars().size() == 3);
        assertTrue(loopStmt.getExecuteUsingVars().getExpression(0).toString().equalsIgnoreCase("1"));

        assertTrue(loopStmt.getBodyStatements().size() == 1);
        assertTrue(loopStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstplsql_nullstmt);

      }

}
