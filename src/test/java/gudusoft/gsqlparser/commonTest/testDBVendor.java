package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testDBVendor extends TestCase {

    public void test1(){
        for (EDbVendor dbVendor : EDbVendor.values()) {
            assertTrue(dbVendor == TGSqlParser.getDBVendorByName(dbVendor.toString().substring(3)));
           // System.out.println(dbVendor.toString().substring(3));
        }
    }

    public void test2(){
        assertTrue(EDbVendor.values().length == TSQLEnv.columnCollationCaseSensitive.length);
        assertTrue(EDbVendor.values().length == TSQLEnv.tableCollationCaseSensitive.length);
        assertTrue(EDbVendor.values().length == TSQLEnv.defaultCollationCaseSensitive.length);
    }

}