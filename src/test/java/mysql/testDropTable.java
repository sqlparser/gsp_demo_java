package test.mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableEffectType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import junit.framework.TestCase;

public class testDropTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "DROP TABLE schema_n.table_n";
        assertTrue(sqlparser.parse() == 0);
        TDropTableSqlStatement dropTableSqlStatement =  (TDropTableSqlStatement)sqlparser.sqlstatements.get(0);
        TTable targetTable = dropTableSqlStatement.getTargetTable();
        assertTrue(targetTable.getEffectType() == ETableEffectType.tetDrop);
        assertTrue(dropTableSqlStatement.getTableNameList().getObjectName(0).getSchemaString().equalsIgnoreCase("schema_n"));
        assertTrue(dropTableSqlStatement.getTableNameList().getObjectName(0).getObjectString().equalsIgnoreCase("table_n"));
        assertTrue(dropTableSqlStatement.getTableNameList().getObjectName(0).toString().equalsIgnoreCase("schema_n.table_n"));
    }

}
