package greenplum;
/*
 * Date: 13-12-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testValues extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "VALUES (1, 'one'), (2, 'two'), (3, 'three');";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TValueClause valueClause = select.getValueClause();
        assertTrue(valueClause.getRows().size() == 3);
        TResultColumnList row0 = valueClause.getRows().get(0);
        assertTrue(row0.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(row0.getResultColumn(1).toString().equalsIgnoreCase("'one'"));

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
        //System.out.println(table2.getTableType());
        assertTrue(table2.getTableType() == ETableSource.subquery);
//        TValueClause valueClause = table2.getSubquery().getValueClause();
        assertTrue(table2.getAliasClause().getAliasName().toString().equalsIgnoreCase("t"));
        assertTrue(table2.getAliasClause().getColumns().getObjectName(0).toString().equalsIgnoreCase("studio"));

        //select = table2.getSubquery();

        TTable valueTable = select.getTables().getTable(1);
        TValueClause valueClause = valueTable.getSubquery().getValueClause();

        assertTrue(valueTable.getAliasClause().getColumns().size() == 2);
        assertTrue(valueTable.getAliasClause().getColumns().getObjectName(0).toString().equalsIgnoreCase("studio"));
        assertTrue(valueClause.getRows().size() == 2);

        TResultColumnList row0 = valueClause.getRows().get(0);
        TResultColumnList row1 = valueClause.getRows().get(1);
        assertTrue(row0.getResultColumn(0).toString().equalsIgnoreCase("'MGM'"));
        assertTrue(row0.getResultColumn(1).toString().equalsIgnoreCase("'Horror'"));
        assertTrue(row1.getResultColumn(0).toString().equalsIgnoreCase("'UA'"));
        assertTrue(row1.getResultColumn(1).toString().toString().equalsIgnoreCase("'Sci-Fi'"));
    }
}
