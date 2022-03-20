package mysql;
/*
 * Date: 13-10-25
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TSetAssignment;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TSetStmt;
import gudusoft.gsqlparser.stmt.mysql.TMySQLIfStmt;
import junit.framework.TestCase;

public class testSet extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SET sort_buffer_size=10000;";
        assertTrue(sqlparser.parse() == 0);

        TSetStmt set = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(set.getAssignments().getElement(0).getParameterName().toString().equalsIgnoreCase("sort_buffer_size"));
        assertTrue(set.getAssignments().getElement(0).getParameterValue().toString().equalsIgnoreCase("10000"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SET GLOBAL sort_buffer_size=1000000, SESSION sort_buffer_size=1000000;";
        assertTrue(sqlparser.parse() == 0);

        TSetStmt set = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(set.getSetStatementType() == ESetStatementType.variable);
        TSetAssignment assignment = set.getAssignments().getElement(0);
        assertTrue(assignment.getSetScope() == ESetScope.global);
        assignment = set.getAssignments().getElement(1);
        assertTrue(assignment.getSetScope() == ESetScope.session);

        assertTrue(set.getAssignments().getElement(0).getParameterName().toString().equalsIgnoreCase("sort_buffer_size"));
        assertTrue(set.getAssignments().getElement(0).getParameterValue().toString().equalsIgnoreCase("1000000"));

    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SET PASSWORD FOR 'bob'@'%.example.org' = PASSWORD('cleartext password');";
        assertTrue(sqlparser.parse() == 0);

        TSetStmt set = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(set.getSetStatementType() == ESetStatementType.password);

        assertTrue(set.getUserName().toString().equalsIgnoreCase("'bob'@'%.example.org'"));
        assertTrue(set.getPassword().toString().equalsIgnoreCase("'cleartext password'"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE PROCEDURE proc1(p1 INT)\n" +
                "    READS SQL DATA\n" +
                "    DETERMINISTIC\n" +
                "BEGIN\n" +
                "IF p1 = -1 THEN\n" +
                "  SET p1 = 10;\n" +
                "END IF;\n" +
                "END;";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedure.getBodyStatements().size() == 1);
        assertTrue(createProcedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmysqlifstmt);
        TMySQLIfStmt ifStmt = (TMySQLIfStmt)createProcedure.getBodyStatements().get(0);
        assertTrue(ifStmt.getCondition().toString().equalsIgnoreCase("p1 = -1"));
        TSetStmt set = (TSetStmt)ifStmt.getThenStmts().get(0);
        assertTrue(set.getSetStatementType() == ESetStatementType.variable);
//        TSetAssignment setAssignment = set.getAssignments().getElement(0);
//
//        TExpression expression = setAssignment.getExpression();
//        assertTrue(expression.getExpressionType() == EExpressionType.assignment_t);
//        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("p1"));
//        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("10"));

        assertTrue(set.getAssignments().getElement(0).getParameterName().toString().equalsIgnoreCase("p1"));
        assertTrue(set.getAssignments().getElement(0).getParameterValue().toString().equalsIgnoreCase("10"));

    }


}
