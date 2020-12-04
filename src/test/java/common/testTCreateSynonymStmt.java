package common;
/*
 * Date: 2010-12-31
 * Time: 14:18:14
 */

import gudusoft.gsqlparser.stmt.TCreateSynonymStmt;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;

public class testTCreateSynonymStmt extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace public synonym GENERAL_PKG for ONAIR.GENERAL_PKG";
        assertTrue(sqlparser.parse() == 0);

        TCreateSynonymStmt synonym = (TCreateSynonymStmt)sqlparser.sqlstatements.get(0);
        assertTrue(synonym.getSynonymName().toString().equalsIgnoreCase("GENERAL_PKG"));
        assertTrue(synonym.getForName().toString().equalsIgnoreCase("ONAIR.GENERAL_PKG"));
    }

}
