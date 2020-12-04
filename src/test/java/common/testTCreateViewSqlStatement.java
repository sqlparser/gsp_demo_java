package common;
/*
 * Date: 2010-10-9
 * Time: 9:41:49
 */

import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.nodes.TViewAliasClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TViewAliasItem;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;

public class testTCreateViewSqlStatement extends TestCase {

    public void testTeradata(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE VIEW X.Y (A, B)\n" +
                "AS SELECT A1, B1 FROM C";
        assertTrue(sqlparser.parse() == 0);
        
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createView.getStatements().size() == 1);
        assertTrue(createView.getStatements().get(0).toString().equalsIgnoreCase("SELECT A1, B1 FROM C"));

        TViewAliasClause aliasClause = createView.getViewAliasClause();
        TViewAliasItem item0 = aliasClause.getViewAliasItemList().getViewAliasItem(0);
        TViewAliasItem item1 = aliasClause.getViewAliasItemList().getViewAliasItem(1);
        assertTrue(item0.toString().equalsIgnoreCase("A"));
        assertTrue(item1.toString().equalsIgnoreCase("B"));
    }
    
    public void testSubquery(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create view empvu80\n" +
                "as select employee_id,last_name,salary\n" +
                "from employees\n" +
                "where employee_id = 80;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createView.getStatements().size() == 1);
        // System.out.println(createView.getStatements().get(0).toString());
    }

    public void testViewAlias(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test1(account_name_alias, account_number_alias) as select account_name, account_number from  AP10_BANK_ACCOUNTS t;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TViewAliasClause aliasClause = createView.getViewAliasClause();
        TViewAliasItem item0 = aliasClause.getViewAliasItemList().getViewAliasItem(0);
        TViewAliasItem item1 = aliasClause.getViewAliasItemList().getViewAliasItem(1);
        assertTrue(item0.toString().equalsIgnoreCase("account_name_alias"));
        assertTrue(item1.toString().equalsIgnoreCase("account_number_alias"));

        TGSqlParser sqlparser2 = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser2.sqltext = "create view a_view (a, b) as select col_1, col_2 from tab_1";
        assertTrue(sqlparser2.parse() == 0);

        TCreateViewSqlStatement createView2 = (TCreateViewSqlStatement)sqlparser2.sqlstatements.get(0);
        TViewAliasClause aliasClause2 = createView2.getViewAliasClause();
        TViewAliasItem item02 = aliasClause2.getViewAliasItemList().getViewAliasItem(0);
        TViewAliasItem item12 = aliasClause2.getViewAliasItemList().getViewAliasItem(1);
        assertTrue(item02.toString().equalsIgnoreCase("a"));
        assertTrue(item12.toString().equalsIgnoreCase("b"));
    }

    public void testSubquery2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW pgemp_view AS SELECT * from pgemp1 UNION ALL SELECT * from pgemp2 UNION ALL SELECT * from pgemp3 UNION ALL SELECT  * from pgemp4;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement subquery = createView.getSubquery();
        while(subquery.getSetOperatorType() != ESetOperatorType.none ){
           // System.out.println(subquery.getRightStmt().getTables().getTable(0).toString());
            subquery = subquery.getLeftStmt();
        }
       // System.out.println(subquery.getTables().getTable(0).toString());
    }

}
