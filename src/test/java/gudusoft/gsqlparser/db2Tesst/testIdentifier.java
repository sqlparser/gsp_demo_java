package gudusoft.gsqlparser.db2Tesst;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testIdentifier extends TestCase {
    public void testQuotedFunction(){
        assertTrue(TSQLEnv.compareIdentifier(EDbVendor.dbvdb2, ESQLDataObjectType.dotFunction,"F_GetRFECustomerName","\"F_GetRFECustomerName\""));
    }
}
