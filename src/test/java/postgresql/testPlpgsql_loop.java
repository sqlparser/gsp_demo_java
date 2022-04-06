package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TExitStmt;
import gudusoft.gsqlparser.stmt.TIfStmt;
import gudusoft.gsqlparser.stmt.TLoopStmt;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_loop extends TestCase {

    public void test1(){
         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
         sqlparser.sqltext = "CREATE FUNCTION get_available_flightid(date) RETURNS SETOF integer AS\n" +
                 "$BODY$\n" +
                 "BEGIN\n" +
                 "\n" +
                 "\tLOOP\n" +
                 "\t\t-- some computations\n" +
                 "\t\tIF count > 0 THEN\n" +
                 "\t\t\tEXIT; -- exit loop\n" +
                 "\t\tEND IF;\n" +
                 "\tEND LOOP;\n" +
                 "\n" +
                 "\tLOOP\n" +
                 "\t\t-- some computations\n" +
                 "\t\tEXIT WHEN count > 0; -- same result as previous example\n" +
                 "\tEND LOOP;\n" +
                 "\n" +
                 "\t<<ablock>>\n" +
                 "\tBEGIN\n" +
                 "\t\t-- some computations\n" +
                 "\t\tIF stocks > 100000 THEN\n" +
                 "\t\t\tEXIT ablock; -- causes exit from the BEGIN block\n" +
                 "\t\tEND IF;\n" +
                 "\t\t-- computations here will be skipped when stocks > 100000\n" +
                 "\tEND;\n" +
                 "\n" +
                 "END\n" +
                 "$BODY$\n" +
                 "LANGUAGE plpgsql;";
         assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getBodyStatements().size() == 3);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
        TLoopStmt loopStmt = (TLoopStmt)stmt;
        assertTrue(loopStmt.getStatements().size() == 1);
        stmt = loopStmt.getStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_ifstmt);
        TIfStmt ifStmt = (TIfStmt)stmt;
        TExpression ifCondition = ifStmt.getCondition();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(ifCondition.getOperatorToken().tokencode == '>');
        assertTrue(ifCondition.getLeftOperand().toString().equalsIgnoreCase("count"));
        assertTrue(ifCondition.getRightOperand().toString().equalsIgnoreCase("0"));

        assertTrue(ifStmt.getStatements().size() == 1);
        stmt = ifStmt.getStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_exitstmt);


        stmt = createFunction.getBodyStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_loopstmt);
        loopStmt = (TLoopStmt)stmt;
        assertTrue(loopStmt.getStatements().size() == 1);
        stmt = loopStmt.getStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_exitstmt);
        TExitStmt exitStmt = (TExitStmt)stmt;
        TExpression whenCondition = exitStmt.getWhenCondition();
        assertTrue(whenCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(whenCondition.getOperatorToken().tokencode == '>');
        assertTrue(whenCondition.getLeftOperand().toString().equalsIgnoreCase("count"));
        assertTrue(whenCondition.getRightOperand().toString().equalsIgnoreCase("0"));

        stmt = createFunction.getBodyStatements().get(2);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_plsql_block);
        TCommonBlock block = (TCommonBlock)stmt;
        assertTrue(block.getLabelName().toString().equalsIgnoreCase("ablock"));

        stmt =block.getBodyStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_ifstmt);
        ifStmt = (TIfStmt)stmt;
        ifCondition = ifStmt.getCondition();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(ifCondition.getOperatorToken().tokencode == '>');
        assertTrue(ifCondition.getLeftOperand().toString().equalsIgnoreCase("stocks"));
        assertTrue(ifCondition.getRightOperand().toString().equalsIgnoreCase("100000"));

        stmt = ifStmt.getStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_exitstmt);
        exitStmt = (TExitStmt)stmt;
        assertTrue(exitStmt.getExitlabelName().toString().equalsIgnoreCase("ablock"));

    }

}
