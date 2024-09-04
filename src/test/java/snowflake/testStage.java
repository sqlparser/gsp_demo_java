package snowflake;

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPathSqlNode;
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
        assertTrue(table.getTableType() == ETableSource.stageReference);
        assertTrue(table.getTableName().getDbObjectType() == EDbObjectType.stage);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("mystage"));
        TPathSqlNode pathSqlNode = table.getStageReference().getStagePath();
        assertTrue(pathSqlNode.toString().equalsIgnoreCase("/sales.csv.gz"));
        assertTrue(pathSqlNode.getPathName().toString().equalsIgnoreCase("sales.csv.gz"));
        assertTrue(pathSqlNode.getRootDirectory().toString().equalsIgnoreCase("/"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = " SELECT $1, $2, $3\n" +
                "    FROM @my_stage/data/2023/12\n" +
                "            (FILE_FORMAT => my_csv_format)\n" +
                "    PATTERN='.*sales_.*\\.csv';";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableType() == ETableSource.stageReference);
        assertTrue(table.getTableName().getDbObjectType() == EDbObjectType.stage);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("my_stage"));
        TPathSqlNode pathSqlNode = table.getStageReference().getStagePath();
        assertTrue(pathSqlNode.toString().equalsIgnoreCase("/data/2023/12"));
        assertTrue(pathSqlNode.getRootDirectory().toString().equalsIgnoreCase("/"));
        assertTrue(pathSqlNode.splitPath()[0].equalsIgnoreCase("data"));
        assertTrue(pathSqlNode.splitPath()[1].equalsIgnoreCase("2023"));
        assertTrue(pathSqlNode.splitPath()[2].equalsIgnoreCase("12"));
    }

}
