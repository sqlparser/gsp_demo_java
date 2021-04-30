package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testGetRawStatement  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".SET MAXERROR 4\n" +
                ".SET RETRY OFF\n" +
                "\n" +
                "SELECT c from t WHERE 1=2;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 3);

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SET level=value\n" +
                "SELECT level, param, 'GMKSA' (TITLE 'OWNER')\n" +
                "   FROM gmksa\n" +
                "   WHERE cycle = '03'";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() == 2);

    }

}
