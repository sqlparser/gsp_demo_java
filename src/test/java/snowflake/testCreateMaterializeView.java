package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateMaterializedSqlStatement;
import junit.framework.TestCase;

public class testCreateMaterializeView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create materialized view exttable_part_mv\n" +
                "  as\n" +
                "  select col2 from exttable_part;";
        assertTrue(sqlparser.parse() == 0);

        TCreateMaterializedSqlStatement createMaterializedSqlStatement = (TCreateMaterializedSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createMaterializedSqlStatement.getViewName().toString().equalsIgnoreCase("exttable_part_mv"));
        assertTrue(createMaterializedSqlStatement.getSubquery().getTables().getTable(0).toString().equalsIgnoreCase("exttable_part"));
        // System.out.println(stageLocation.getStageName().toString());
    }
}
