package postgresql;
/*
 * Date: 14-2-7
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testValueListInFromClause extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT * FROM (VALUES (1, 'one'), (2, 'two'), (3, 'three')) AS t (num,letter);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.rowList);
        TMultiTargetList rowList =  table.getRowList();
        assertTrue(rowList.size() == 3);
        TMultiTarget row = rowList.getMultiTarget(0);
        assertTrue(row.getColumnList().size() == 2);
        assertTrue(row.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(row.getColumnList().getResultColumn(1).toString().equalsIgnoreCase("'one'"));

        row = rowList.getMultiTarget(2);
        assertTrue(row.getColumnList().size() == 2);
        assertTrue(row.getColumnList().getResultColumn(0).toString().equalsIgnoreCase("3"));
        assertTrue(row.getColumnList().getResultColumn(1).toString().equalsIgnoreCase("'three'"));

        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("t"));
        assertTrue(aliasClause.getColumns().size() == 2);
        assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("num"));
        assertTrue(aliasClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("letter"));
    }
}
