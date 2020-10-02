package test.hive;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.hive.THiveFromQuery;
import junit.framework.TestCase;


public class testCTE  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "with q1 as ( select key from src where key = '5')\n" +
                "select *\n" +
                "from q1;\n";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("q1"));
        assertTrue(select.getCteList().size() == 1);
        TCTE cte = select.getCteList().getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("q1"));
        TSelectSqlStatement s1 = cte.getSubquery();
        assertTrue(s1.tables.getTable(0).toString().equalsIgnoreCase("src"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "with q1 as (select * from src where key= '5')\n" +
                "from q1\n" +
                "select *;";
        assertTrue(sqlparser.parse() == 0);

        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement fromQuery = (TSelectSqlStatement)sqlStatement;


        assertTrue(fromQuery.tables.getTable(0).toString().equalsIgnoreCase("q1"));
        assertTrue(fromQuery.getCteList().size() == 1);
        TCTE cte = fromQuery.getCteList().getCTE(0);
        assertTrue(cte.getTableName().toString().equalsIgnoreCase("q1"));
        TSelectSqlStatement s1 = cte.getSubquery();
        assertTrue(s1.tables.getTable(0).toString().equalsIgnoreCase("src"));
    }
}
