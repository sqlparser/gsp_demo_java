package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testPlpgsql_insert extends TestCase {

    public void test1(){

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
