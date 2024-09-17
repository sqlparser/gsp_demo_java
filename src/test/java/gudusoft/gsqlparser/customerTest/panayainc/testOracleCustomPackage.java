package gudusoft.gsqlparser.customerTest.panayainc;
/*
 * Date: 2010-11-2
 * Time: 14:20:26
 */

import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;

public class testOracleCustomPackage extends TestCase {

    // custom package syntax.
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PRIVATE +"java\\oracle\\panayainc\\custompackage.sql";
        assertTrue(sqlparser.parse() == 0);
    }

}
