package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCTEList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testCTE extends TestCase {

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


