package oracle;
/*
 * Date: 13-1-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testPivot extends TestCase {

    public void testOracle1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * FROM pivot_table\n" +
                "  UNPIVOT (yearly_total FOR order_mode IN (store AS 'direct', internet AS 'online'))\n" +
                "  ORDER BY year, order_mode;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getTargetTable().getTableType() == ETableSource.pivoted_table);
        TTable table = select.tables.getTable(0);
        assertTrue(table.toString().equalsIgnoreCase("pivot_table"));

        TPivotedTable pivotedTable = select.getTargetTable().getPivotedTable();

        TPivotClause pivotClause = pivotedTable.getPivotClauseList().getElement(0) ;
        assertTrue(pivotClause.getType() == TPivotClause.unpivot);
        assertTrue(pivotClause.getValueColumnList().getObjectName(0).toString().equalsIgnoreCase("yearly_total"));
        assertTrue(pivotClause.getPivotColumnList().getObjectName(0).toString().equalsIgnoreCase("order_mode"));

        TUnpivotInClause inClause = pivotClause.getUnpivotInClause();
        TUnpivotInClauseItem item0 = inClause.getItems().getElement(0);
        assertTrue(item0.getColumn().toString().equalsIgnoreCase("store"));
        assertTrue(item0.getConstant().toString().equalsIgnoreCase("'direct'"));

        TUnpivotInClauseItem item1 = inClause.getItems().getElement(1);
        assertTrue(item1.getColumn().toString().equalsIgnoreCase("internet"));
        assertTrue(item1.getConstant().toString().equalsIgnoreCase("'online'"));

    }

    public void testOracle2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * FROM orders\n" +
                "PIVOT(SUM(order_total) \n" +
                "\t\tFOR order_mode IN (SELECT 'direct' AS Store, 'online' AS Internet FROM orders));";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getTargetTable().getTableType() == ETableSource.pivoted_table);
        TTable table = select.tables.getTable(0);
        assertTrue(table.toString().equalsIgnoreCase("orders"));
        TPivotedTable pivotedTable = select.getTargetTable().getPivotedTable();
        TPivotClause pivotClause = pivotedTable.getPivotClauseList().getElement(0);
        assertTrue(pivotClause.getType() == TPivotClause.pivot);

        TPivotInClause inClause = pivotClause.getPivotInClause();
        select = inClause.getSubQuery();
        assertTrue(select.getResultColumnList().size() == 2);
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("orders"));

    }
}
