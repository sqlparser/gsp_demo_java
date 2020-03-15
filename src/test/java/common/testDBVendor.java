package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testDBVendor extends TestCase {

    public void test1(){
        for (EDbVendor dbVendor : EDbVendor.values()) {
            assertTrue(dbVendor == TGSqlParser.getDBVendorByName(dbVendor.toString().substring(3)));
           // System.out.println(dbVendor.toString().substring(3));
        }
    }

}