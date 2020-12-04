package dag;
/*
 * Date: 11-4-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testMultiSetOperator extends TestCase {

    public static void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT CAST( MULTISET( SELECT empno, empname FROM emp) AS emp_tab_t ) emptab\n" +
                "FROM DUAL;";
       assertTrue(sqlparser.parse()==0);
    }

}
