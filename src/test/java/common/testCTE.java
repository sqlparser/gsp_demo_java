package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;


public class testCTE extends TestCase {

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


