package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 14-7-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testResultColumn extends TestCase {
      public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT NULL(VARCHAR(512) )," +
                "CASE WHEN DATABASENAME <> 'DBC' THEN 'TYPE' ELSE '' END (NAMED TABLE_TYPE), " +
                "COMMENTSTRING(VARCHAR(254),NAMED REMARKS) FROM DBC.TABLES";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TResultColumnList columns = select.getResultColumnList();
        assertTrue(columns.getResultColumn(0).toString().equalsIgnoreCase("NULL(VARCHAR(512) )"));
        assertTrue(columns.getResultColumn(1).toString().equalsIgnoreCase("CASE WHEN DATABASENAME <> 'DBC' THEN 'TYPE' ELSE '' END (NAMED TABLE_TYPE)"));
        assertTrue(columns.getResultColumn(2).toString().equalsIgnoreCase("COMMENTSTRING(VARCHAR(254),NAMED REMARKS)"));


       }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE VOLATILE SET TABLE CORR_ADD_SOURCE AS\n" +
                "(\n" +
                "SELECT\n" +
                "\tACCT_NUM\n" +
                "\t,CORR_NUMBER\n" +
                "FROM DB50_AKG.BASSRNC_CORR_SOURCE\n" +
                "GROUP BY 1,2\n" +
                ") WITH DATA\n" +
                "UNIQUE PRIMARY INDEX (ACCT_NUM)\n" +
                "ON COMMIT PRESERVE ROWS";

        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = createTableSqlStatement.getSubQuery();

        TResultColumnList columns = select.getResultColumnList();
        assertTrue(columns.getResultColumn(0).toString().equalsIgnoreCase("ACCT_NUM"));

        TTable table  = createTableSqlStatement.tables.getTable(0);
       // System.out.print(table.toString());
       // System.out.print(table.getLinkedColumns().getObjectName(0).toScript());
        assertTrue(table.getLinkedColumns().getObjectName(0).getLocation() == ESqlClause.selectList);

    }
}
