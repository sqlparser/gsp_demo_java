package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testFastExportCmd extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = ".EXPORT FILE=a/b/file.txt\n" +
                "Select * from abc";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.size() ==2);

    }

}