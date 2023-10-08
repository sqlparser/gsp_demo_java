package snowflake;

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testStage extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "select t.$1, t.$2, t.$6, t.$7 from @mystage/sales.csv.gz";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("mystage/sales.csv.gz"));
        assertTrue(table.getTableName().getDbObjectType() == EDbObjectType.stage);
        assertTrue(table.getStageName().equalsIgnoreCase("mystage"));
    }
}
