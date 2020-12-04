package common;
/*
 * Date: 2010-11-3
 * Time: 10:24:10
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;

public class testTOrderByItem extends TestCase {

    public void testSortType(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test3 as select account_name, account_number from  AP10_BANK_ACCOUNTS t where t.bank_name = 'Bank of Mars' and t.branch_city = 'Giant Crater' order by t.account_name asc;";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = (TSelectSqlStatement)createView.getSubquery();
        TOrderByItem item0 = select.getOrderbyClause().getItems().getOrderByItem(0);
        assertTrue(item0.getSortKey().toString().equalsIgnoreCase("t.account_name"));
        assertTrue(item0.getSortType() == TBaseType.srtAsc);
    }

}
