package test.mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import junit.framework.TestCase;

public class testDelete  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "DELETE LOW_PRIORITY from t1 where a=1";
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement deleteSqlStatement = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(deleteSqlStatement.getTargetTable().toString().equalsIgnoreCase("t1"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "delete LOW_PRIORITY QUICK IGNORE from t5 where name='B' and id=1;";
        assertTrue(sqlparser.parse() == 0);

        TDeleteSqlStatement deleteSqlStatement = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        //System.out.print(deleteSqlStatement.getTargetTable().toString());
        assertTrue(deleteSqlStatement.getTargetTable().toString().equalsIgnoreCase("t5"));
    }

}
