package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testCTE extends TestCase {

    public void testrecursiveCTE() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "create view v3 as \n" +
                "WITH RECURSIVE employee_recursive(distance, employee_name, manager_name) AS (\n" +
                "SELECT 1, employee_name, manager_name\n" +
                "FROM employee\n" +
                "WHERE manager_name = 'Mary'\n" +
                "UNION ALL\n" +
                "SELECT er.distance + 1, e.employee_name, e.manager_name\n" +
                "FROM employee_recursive er, employee e\n" +
                "WHERE er.employee_name = e.manager_name\n" +
                ")\n" +
                "SELECT distance, employee_name FROM employee_recursive;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement) sqlparser.sqlstatements.get(0);

        TSelectSqlStatement select = createViewSqlStatement.getSubquery();
        // employee_recursive in SELECT distance, employee_name FROM employee_recursive is CTE
        TTable table2 = select.getRelations().get(0);
        assertTrue(table2.toString().equalsIgnoreCase("employee_recursive"));
        assertTrue(table2.isCTEName());


        TCTEList ctelist = select.getCteList();

        // aaa in from clause of : with aaa as (select nameen from aaa) is not CTE
        TCTE cte0 = ctelist.getCTE(0);
        TSelectSqlStatement subquery = cte0.getSubquery();
        TSelectSqlStatement rightSubQuery = subquery.getRightStmt();


        TTable table = rightSubQuery.getRelations().get(0);
        assertTrue(table.toString().equalsIgnoreCase("employee_recursive"));
        assertTrue(table.getStartToken().lineNo == 8);
        assertTrue(table.isCTEName());
        assertTrue(table.getCTE() == cte0);

    }

    public void testSelectListinSubQueryCTE() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqltext = "with table_a as (\n" +
                "  select id ,ssid from (\n" +
                "select id,ssid from table_a\n" +
                "  )t)\n" +
                "select * from table_a";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        // table_a in select * from table_a is CTE
        TTable table2 = select.getRelations().get(0);
        assertTrue(table2.toString().equalsIgnoreCase("table_a"));
        assertTrue(table2.isCTEName());


        TCTEList ctelist = select.getCteList();

        // aaa in from clause of : with aaa as (select nameen from aaa) is not CTE
        TCTE cte0 = ctelist.getCTE(0);
        TSelectSqlStatement subquery = cte0.getSubquery();


        TResultColumn resultColumn = subquery.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().getObjectOperand().getSourceTable().getTableType() == ETableSource.subquery);
        assertTrue(resultColumn.getExpr().getObjectOperand().getSourceTable().getStartToken().lineNo == 2);


        TSelectSqlStatement subquery2 = subquery.getRelations().get(0).getSubquery();
        TTable table = subquery2.getRelations().get(0);
        assertTrue(table.toString().equalsIgnoreCase("table_a"));
        assertTrue(table.getStartToken().lineNo == 3);
        assertTrue(!subquery2.getRelations().get(0).isCTEName());
        assertTrue(table.getCTE() == null);
        assertTrue(!subquery2.tables.getTable(0).isCTEName());
        TResultColumn resultColumn0 = subquery2.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn0.getExpr().toString().equalsIgnoreCase("id"));
        TResultColumn resultColumn1 = subquery2.getResultColumnList().getResultColumn(1);
        assertTrue(resultColumn1.getExpr().toString().equalsIgnoreCase("ssid"));
        assertTrue (resultColumn0.getExpr().getObjectOperand().getSourceTable().getStartToken().lineNo == 3);
    }

    public void testFalseCTETable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgaussdb);
        sqlparser.sqltext = "with aaa as (select nameen from aaa)\n" +
                ", bbb as (select nameen from aaa)\n" +
                "select * from aaa";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        // aaa in select * from aaa is CTE
        TTable table2 = select.getRelations().get(0);
        assertTrue(table2.toString().equalsIgnoreCase("aaa"));
        assertTrue(table2.isCTEName());

        // aaa in  bbb as (select nameen from aaa) is CTE
        TCTEList ctelist = select.getCteList();
        TCTE cte1 = ctelist.getCTE(1);
        TTable table1 = cte1.getSubquery().getRelations().get(0);
        assertTrue(table1.toString().equalsIgnoreCase("aaa"));
        assertTrue(table1.isCTEName());

        // aaa in from clause of : with aaa as (select nameen from aaa) is not CTE
        TCTE cte0 = ctelist.getCTE(0);
        TTable table0 = cte0.getSubquery().getRelations().get(0);
        assertTrue(table0.toString().equalsIgnoreCase("aaa"));
        assertTrue(!table0.isCTEName());

    }

    public void testTemporaryTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "WITH dataset1 AS (SELECT 1 FROM dual),dataset2 AS (SELECT 2 FROM dual) SELECT 3 from dual union all SELECT 4 from dual";
        assertTrue(sqlparser.parse() == 0);

//        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//        System.out.println(select.isQueryOfCTE());
//        System.out.println(select.getCteList().getCTE(0).getSubquery().isQueryOfCTE());
//        System.out.println(select.getCteList().getCTE(1).getSubquery().isQueryOfCTE());
//
//        sqlparser.sqltext = "WITH dataset1(param) AS (SELECT 1 FROM dual),dataset2 AS (SELECT 2 FROM dual) SELECT 3 from dual union all SELECT 4 from dual";
//        assertTrue(sqlparser.parse() == 0);
//
//        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//        System.out.println(select.isQueryOfCTE());
//        System.out.println(select.getCteList().getCTE(0).getSubquery().isQueryOfCTE());
//        System.out.println(select.getCteList().getCTE(1).getSubquery().isQueryOfCTE());
    }
}


