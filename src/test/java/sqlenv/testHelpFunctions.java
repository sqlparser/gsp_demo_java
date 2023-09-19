package sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import junit.framework.TestCase;

public class testHelpFunctions extends TestCase {

    public void test1() {
        EDbVendor dbVendor = EDbVendor.dbvoracle;
        ESQLDataObjectType dataObjectType = ESQLDataObjectType.dotTable;

        assertTrue(TSQLEnv.matchSubObjectNameToWhole(EDbVendor.dbvoracle,ESQLDataObjectType.dotTable, "a","a"));
        assertTrue(TSQLEnv.matchSubObjectNameToWhole(EDbVendor.dbvoracle,ESQLDataObjectType.dotTable, "a","scott.a"));
        assertTrue(!TSQLEnv.matchSubObjectNameToWhole(EDbVendor.dbvoracle,ESQLDataObjectType.dotTable, "b","scott.a"));

        assertTrue(TSQLEnv.matchSubObjectNameToWhole( EDbVendor.dbvmssql ,ESQLDataObjectType.dotTable, "a","[a]"));
        assertTrue(TSQLEnv.matchSubObjectNameToWhole( EDbVendor.dbvbigquery ,ESQLDataObjectType.dotTable, "`a`","a"));
        assertTrue(TSQLEnv.matchSubObjectNameToWhole( EDbVendor.dbvbigquery ,ESQLDataObjectType.dotTable, "RETAIL_PROD_EXCEPTIONS_SOURCE","`data`.`RETAIL_PROD_EXCEPTIONS_SOURCE`"));

        assertTrue(TSQLEnv.matchSubObjectNameToWhole( EDbVendor.dbvbigquery ,ESQLDataObjectType.dotTable, "RETAIL_PROD_EXCEPTIONS_SOURCE","`data.RETAIL_PROD_EXCEPTIONS_SOURCE`"));

    }


    public void  test2(){
        assertTrue(TBaseType.removePrefixOrSuffixQuoteChar("`abc").equalsIgnoreCase("abc"));
        assertTrue(TBaseType.removePrefixOrSuffixQuoteChar("abc`").equalsIgnoreCase("abc"));
    }
}
