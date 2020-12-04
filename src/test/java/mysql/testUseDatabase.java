package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import junit.framework.TestCase;


public class testUseDatabase extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "use dbname";
        assertTrue(sqlparser.parse() == 0);

        TUseDatabase use = (TUseDatabase)sqlparser.sqlstatements.get(0);
        assertTrue(use.getDatabaseName().toString().equalsIgnoreCase("dbname"));

    }

}
