package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TCreateWarehouseStmt;
import junit.framework.TestCase;

public class testCreateWarehouse extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace warehouse mywarehouse with\n" +
                "  warehouse_size='X-SMALL'\n" +
                "  auto_suspend = 120\n" +
                "  auto_resume = true\n" +
                "  initially_suspended=true;";
        assertTrue(sqlparser.parse() == 0);

        TCreateWarehouseStmt createWarehouseStmt = (TCreateWarehouseStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createWarehouseStmt.getWarehouseName().toString().equalsIgnoreCase("mywarehouse"));
    }
}
