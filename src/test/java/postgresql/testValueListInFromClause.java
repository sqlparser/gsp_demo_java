package postgresql;
/*
 * Date: 14-2-7
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testValueListInFromClause extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT * FROM (VALUES (1, 'one'), (2, 'two'), (3, 'three')) AS t (num,letter);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.subquery);


        ArrayList<TResultColumnList> rowList =  table.getSubquery().getValueClause().getRows();
        assertTrue(rowList.size() == 3);
        TResultColumnList row = rowList.get(0);
        assertTrue(row.size() == 2);
        assertTrue(row.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(row.getResultColumn(1).toString().equalsIgnoreCase("'one'"));

        row = rowList.get(2);
        assertTrue(row.size() == 2);
        assertTrue(row.getResultColumn(0).toString().equalsIgnoreCase("3"));
        assertTrue(row.getResultColumn(1).toString().equalsIgnoreCase("'three'"));

        TAliasClause aliasClause = table.getAliasClause();
        assertTrue(aliasClause.getAliasName().toString().equalsIgnoreCase("t"));
        assertTrue(aliasClause.getColumns().size() == 2);
        assertTrue(aliasClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("num"));
        assertTrue(aliasClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("letter"));
    }
}
