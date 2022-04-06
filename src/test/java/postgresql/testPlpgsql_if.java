package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_if extends TestCase {
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


    public void testElseif(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION process_emp_audit() RETURNS TRIGGER AS $emp_audit$\n" +
                "BEGIN\n" +
                "\t---- Create a row in emp_audit to reflect the operation performed on emp,\n" +
                "\t-- make use of the special variable TG_OP to work out the operation.\n" +
                "\tIF (TG_OP = 'DELETE') THEN\n" +
                "\tINSERT INTO emp_audit SELECT 'D', now(), user, OLD.*\n" +
                "\t;\n" +
                "\tRETURN OLD;\n" +
                "\tELSIF (TG_OP = 'UPDATE') THEN\n" +
                "\tINSERT INTO emp_audit SELECT 'U', now(), user, NEW.*\n" +
                "\t;\n" +
                "\tRETURN NEW;\n" +
                "\tELSIF (TG_OP = 'INSERT') THEN\n" +
                "\tINSERT INTO emp_audit SELECT 'I', now(), user, NEW.*\n" +
                "\t;\n" +
                "\tRETURN NEW;\n" +
                "\tEND IF;\n" +
                "\tRETURN NULL; -- result is ignored since this is an AFTER trigger\n" +
                "END;\n" +
                "$emp_audit$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("process_emp_audit"));
        assertTrue(createFunction.getBodyStatements().size() == 2);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        TIfStmt ifStmt = (TIfStmt)stmt;
        TExpression ifCondition = ifStmt.getCondition();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.parenthesis_t);
        ifCondition = ifCondition.getLeftOperand();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(ifCondition.toString().equalsIgnoreCase("TG_OP = 'DELETE'"));
        assertTrue(ifStmt.getThenStatements().size() == 2);

        stmt = ifStmt.getThenStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)stmt;
        assertTrue(insert.getTargetTable().toString().equalsIgnoreCase("emp_audit"));
        TSelectSqlStatement subquery = insert.getSubQuery();
        assertTrue(subquery.getResultColumnList().size() == 4);
        assertTrue(subquery.getResultColumnList().getResultColumn(3).toString().equalsIgnoreCase("OLD.*"));

        assertTrue(ifStmt.getElseifStatements().size() == 2);
        TElsifStmt elsifStmt = (TElsifStmt)ifStmt.getElseifStatements().get(0);

        ifCondition = elsifStmt.getCondition();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.parenthesis_t);
        ifCondition = ifCondition.getLeftOperand();
        assertTrue(ifCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(ifCondition.toString().equalsIgnoreCase("TG_OP = 'UPDATE'"));
        assertTrue(elsifStmt.getThenStatements().size() == 2);

        stmt = elsifStmt.getThenStatements().get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstinsert);
         insert = (TInsertSqlStatement)stmt;
        assertTrue(insert.getTargetTable().toString().equalsIgnoreCase("emp_audit"));
        subquery = insert.getSubQuery();
        assertTrue(subquery.getResultColumnList().size() == 4);
        assertTrue(subquery.getResultColumnList().getResultColumn(3).toString().equalsIgnoreCase("NEW.*"));


        stmt = ifStmt.getThenStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_returnstmt);
        TReturnStmt returnStmt = (TReturnStmt)stmt;
        assertTrue(returnStmt.getExpression().toString().equalsIgnoreCase("OLD"));

        stmt = createFunction.getBodyStatements().get(1);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sst_returnstmt);
        returnStmt = (TReturnStmt)stmt;
        assertTrue(returnStmt.getExpression().toString().equalsIgnoreCase("NULL"));

    }

}
