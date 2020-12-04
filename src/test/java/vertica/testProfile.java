package vertica;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.vertica.TProfileStmt;
import junit.framework.TestCase;


public class testProfile extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "PROFILE SELECT customer_name, annual_income FROM public.customer_dimension \n" +
                "WHERE (customer_gender, annual_income) IN (SELECT customer_gender, MAX(annual_income) \n" +
                "FROM public.customer_dimension GROUP BY customer_gender);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstProfile);
        TProfileStmt profileStmt = (TProfileStmt) sqlparser.sqlstatements.get(0);
        assertTrue(profileStmt.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)profileStmt.getStatement();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("public.customer_dimension"));
    }
}
