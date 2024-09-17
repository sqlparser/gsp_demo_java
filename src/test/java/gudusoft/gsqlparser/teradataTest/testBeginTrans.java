package gudusoft.gsqlparser.teradataTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;


public class testBeginTrans extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "USING(AA1 int,AA2 int) BT;\n" +
                "EXEC insert1(:AA1);\n" +
                "EXEC insert2(:AA2);";
        assertTrue(sqlparser.parse() == 0);

    }
}
