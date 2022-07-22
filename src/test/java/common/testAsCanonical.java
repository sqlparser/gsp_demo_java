package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testAsCanonical extends TestCase {

    public void testRemoveParenthesis(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "((select name\n" +
                "from emp, dept\n" +
                "where emp.deptid in (\n" +
                "\tselect dept.id from t\n" +
                "\t)\n" +
                ")); -- comment11";
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select name\n" +
                "from emp, dept\n" +
                "where emp.deptid in (\n" +
                "\tselect dept.id from t\n" +
                "\t)"));
    }

    public void testReplaceConstant(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select name,1,'tiger'\n" +
                "from emp, dept\n" +
                "where emp.deptid in (1,2,3,4)\n" +
                "and emp.id = 10\n" +
                "and emp.name = 'scott'";
        assertTrue(sqlparser.parse() == 0);
        assertTrue (sqlparser.sqlstatements.get(0).asCanonical().trim().equalsIgnoreCase("select name,1,'tiger'\n" +
                "from emp, dept\n" +
                "where emp.deptid in (999)\n" +
                "and emp.id = 999\n" +
                "and emp.name = 'placeholder_str'"));
    }

}
