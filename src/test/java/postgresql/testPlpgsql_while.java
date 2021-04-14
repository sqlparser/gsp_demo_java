package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TLoopStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_while extends TestCase {
       public void test1(){
           TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
           sqlparser.sqltext = "CREATE FUNCTION get_available_flightid(date) RETURNS SETOF integer AS\n" +
                   "$BODY$\n" +
                   "BEGIN\n" +
                   "\n" +
                   "\tWHILE amount_owed > 0 AND gift_certificate_balance > 0 LOOP\n" +
                   "\t-- some computations here\n" +
                   "\t\tnull;\n" +
                   "\tEND LOOP;\n" +
                   "\t\n" +
                   "\tWHILE NOT done LOOP\n" +
                   "\t-- some computations here\n" +
                   "\t\tnull;\n" +
                   "\tEND LOOP;\n" +
                   "\n" +
                   "END\n" +
                   "$BODY$\n" +
                   "LANGUAGE plpgsql;";
           assertTrue(sqlparser.parse() == 0);

           TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
          assertTrue(createFunction.getBodyStatements().size() == 2);
          TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
          TLoopStmt loopStmt = (TLoopStmt)stmt;
           assertTrue(loopStmt.getKind() == TLoopStmt.while_loop);
           assertTrue(loopStmt.getCondition().getExpressionType() == EExpressionType.logical_and_t);
           assertTrue(loopStmt.getCondition().getLeftOperand().toString().equalsIgnoreCase("amount_owed > 0"));
           assertTrue(loopStmt.getCondition().getRightOperand().toString().equalsIgnoreCase("gift_certificate_balance > 0"));

           assertTrue(loopStmt.getBodyStatements().size() == 1);
           assertTrue(loopStmt.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstplsql_nullstmt);


            stmt = createFunction.getBodyStatements().get(1);
          assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
          loopStmt = (TLoopStmt)stmt;
           assertTrue(loopStmt.getKind() == TLoopStmt.while_loop);
           assertTrue(loopStmt.getCondition().getExpressionType() == EExpressionType.logical_not_t);
           assertTrue(loopStmt.getCondition().getRightOperand().toString().equalsIgnoreCase("done"));

        }


}
