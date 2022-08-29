package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testUTF8 extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        //sqlparser.setSqlCharset("UTF-8");
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"teradata\\utf-8-LEFT SINGLE QUOTATION MARK.sql";

        assertTrue(sqlparser.parse() == 0);

    }
}
