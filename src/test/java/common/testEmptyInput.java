package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testEmptyInput extends TestCase {

        public void testCollate(){
            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
            sqlparser.sqltext = "";
            assertTrue(sqlparser.parse() == 0);
        }
}
