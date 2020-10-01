package test.hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.hive.THiveLateralView;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLateralView extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT * FROM exampleTable\n" +
                "        LATERAL VIEW explode(col1) myTable1 AS myCol1\n" +
                "        LATERAL VIEW explode(myCol1) myTable2 AS myCol2;";
        //System.out.println(sqlparser.sqltext);
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.objectname);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("exampleTable"));
        assertTrue(table.getLateralViewList().size() == 2);

        THiveLateralView view = table.getLateralViewList().get(0);
        TFunctionCall call = view.getUdtf();
        assertTrue(call.getFunctionName().toString().equalsIgnoreCase("explode"));
        assertTrue(call.getArgs().getExpression(0).toString().equalsIgnoreCase("col1"));
        assertTrue(view.getTableAlias().getAliasName().toString().equalsIgnoreCase("myTable1"));
        assertTrue(view.getColumnAliasList().size() == 1);
        assertTrue(view.getColumnAliasList().getObjectName(0).toString().equalsIgnoreCase("myCol1"));

        view = table.getLateralViewList().get(1);
        call = view.getUdtf();
        assertTrue(call.getFunctionName().toString().equalsIgnoreCase("explode"));
        assertTrue(call.getArgs().getExpression(0).toString().equalsIgnoreCase("myCol1"));
        assertTrue(view.getTableAlias().getAliasName().toString().equalsIgnoreCase("myTable2"));
        assertTrue(view.getColumnAliasList().size() == 1);
        assertTrue(view.getColumnAliasList().getObjectName(0).toString().equalsIgnoreCase("myCol2"));

    }
}
