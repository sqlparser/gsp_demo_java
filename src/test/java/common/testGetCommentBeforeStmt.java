package common;

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

public class testGetCommentBeforeStmt extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "-- comment \n" +
                "/* fafaf */\n" +
                "SELECT deptno, \n" +
                "\t\t Count()  num_emp, \n" +
                "\t\t SUM(sal) sal_sum \n" +
                "FROM   scott.emp ";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        System.out.println(sqlStatement.getCommentBeforeNode());
        String desiredOut = "-- comment \n" +
                "/* fafaf */";
        assertTrue(sqlStatement.getCommentBeforeNode().equalsIgnoreCase(desiredOut));
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "/* fafaf */\n" +
                "\n" +
                "-- comment \n" +
                "SELECT deptno, \n" +
                "\t\t Count()  num_emp, \n" +
                "\t\t SUM(sal) sal_sum \n" +
                "FROM   scott.emp ";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);

        String disiredOut = "/* fafaf */\n" +
                "\n" +
                "-- comment";
        assertTrue(sqlStatement.getCommentBeforeNode().trim().equalsIgnoreCase(disiredOut));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "/* fafaf */\n" +
                "-- comment \n" +
                "\n" +
                "\n" +
                "SELECT deptno, \n" +
                "\t\t Count()  num_emp, \n" +
                "\t\t SUM(sal) sal_sum \n" +
                "FROM   scott.emp";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
       // System.out.println(sqlStatement.getCommentBeforeNode());
        String desiredOut = "/* fafaf */\n" +
                "-- comment";
        assertTrue(sqlStatement.getCommentBeforeNode().trim().equalsIgnoreCase(desiredOut));
    }

}
