package common;
/*
 * Date: 2010-10-9
 * Time: 11:19:25
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSet;

public class testTMssqlSet extends TestCase {

    public void testXmlMethnod(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "set @abc.QUERY('/path/to/b')";
//        sqlparser.sqltext = "set @abc = 1";
        assertTrue(sqlparser.parse() == 0);
        TMssqlSet setstmt = (TMssqlSet)sqlparser.sqlstatements.get(0);
//        System.out.println(setstmt.getSetType());
//        System.out.println(setstmt.toString());
        assertTrue(setstmt.getSetType() == TBaseType.mstXmlMethod);

        //TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        //assertTrue(select.getSelectDistinct().getDistinctType() == TBaseType.dtDistinct);
    }

}
