package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TUseWarehouse;
import junit.framework.TestCase;

public class testUseWarehouse extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE WAREHOUSE wName";
        assertTrue(sqlparser.parse() == 0);

        TUseWarehouse useWarehouse = (TUseWarehouse)sqlparser.sqlstatements.get(0);
        assertTrue(useWarehouse.getWarehouseName().toString().equalsIgnoreCase("wName"));
    }
}
