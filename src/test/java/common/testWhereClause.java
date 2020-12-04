package common;/**
 * Created by tako on 2014/11/21.
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testWhereClause extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT EMPLOYEE_NAME FROM EMPLOYEE WHERE EMPLOYEE_GRADE > 22";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause whereClause = select.getWhereClause();
        assertTrue(whereClause.getStartToken().columnNo == 36);
    }

}
