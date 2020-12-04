package netezza;
/*
 * Date: 14-1-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testMinusSetToString extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "CREATE TABLE X AS \n" +
                "SELECT A FROM B\n" +
                "minus\n" +
                "SELECT A FROM C";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement ct  = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = ct.getSubQuery();
        assertTrue(select.toString() != null);

    }


}
