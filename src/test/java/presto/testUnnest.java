package presto;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TUnnestClause;
import gudusoft.gsqlparser.nodes.TValueClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testUnnest extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "SELECT numbers, animals, n, a\n" +
                "FROM (\n" +
                "  VALUES\n" +
                "    (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                "    (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                ") AS x (numbers, animals)\n" +
                "CROSS JOIN UNNEST(numbers, animals) AS t (n, a);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table0 = select.getTables().getTable(0);
        assertTrue(table0.getTableType() == ETableSource.subquery);
        TSelectSqlStatement subquery = table0.getSubquery();
        TValueClause valueClause = subquery.getValueClause();
        assertTrue(valueClause.getRows().size() == 2);
        TResultColumnList columns = valueClause.getRows().get(0);
        assertTrue(columns.size() == 2);
        assertTrue(columns.getResultColumn(0).getExpr().toString().equalsIgnoreCase("ARRAY[2, 5]"));
        assertTrue(columns.getResultColumn(1).getExpr().toString().equalsIgnoreCase("ARRAY['dog', 'cat', 'bird']"));

        TTable table1  = select.getTables().getTable(1);
        assertTrue (table1.getTableType() == ETableSource.unnest);
        TUnnestClause unnestClause = table1.getUnnestClause();
        assertTrue(unnestClause.getColumns().size() == 2);
        assertTrue(unnestClause.getColumns().getObjectName(0).toString().equalsIgnoreCase("numbers"));
        assertTrue(unnestClause.getColumns().getObjectName(1).toString().equalsIgnoreCase("animals"));

    }

}
