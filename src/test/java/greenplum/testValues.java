package greenplum;
/*
 * Date: 13-12-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TValueClause;
import gudusoft.gsqlparser.nodes.TValueRowItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testValues extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "VALUES (1, 'one'), (2, 'two'), (3, 'three');";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TValueClause valueClause = select.getValueClause();
        assertTrue(valueClause.getValueRows().size() == 3);
        TValueRowItem rowItem = valueClause.getValueRows().getValueRowItem(0);
        assertTrue(rowItem.getExprList().getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(rowItem.getExprList().getExpression(1).toString().equalsIgnoreCase("'one'"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "SELECT f.* FROM films f, (VALUES('MGM', 'Horror'), ('UA', \n" +
                "'Sci-Fi')) AS t (studio, kind) WHERE f.studio = t.studio AND \n" +
                "f.kind = t.kind;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.joins.size() == 2 );
        TTable table2 = select.joins.getJoin(1).getTable();
        assertTrue(table2.getTableType() == ETableSource.subquery);
        assertTrue(table2.getAliasClause().getAliasName().toString().equalsIgnoreCase("t"));
        assertTrue(table2.getAliasClause().getColumns().getObjectName(0).toString().equalsIgnoreCase("studio"));
        select = table2.getSubquery();

        TValueClause valueClause = select.getValueClause();
        assertTrue(valueClause.getValueRows().size() == 2);
        TValueRowItem rowItem = valueClause.getValueRows().getValueRowItem(0);
        assertTrue(rowItem.getExprList().getExpression(0).toString().equalsIgnoreCase("'MGM'"));
        assertTrue(rowItem.getExprList().getExpression(1).toString().equalsIgnoreCase("'Horror'"));
        rowItem = valueClause.getValueRows().getValueRowItem(1);
        assertTrue(rowItem.getExprList().getExpression(0).toString().equalsIgnoreCase("'UA'"));
        assertTrue(rowItem.getExprList().getExpression(1).toString().equalsIgnoreCase("'Sci-Fi'"));
    }
}
