package common;
/*
 * Date: 13-11-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testTmp extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "set define off;\nupdate test set test_col='&VAR.' where test_where='someValue';\ncommit;";
        assertTrue(sqlparser.parse() == 0);
        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(1);
        //sqlparser.freeParseTable();
        //System.out.println(updateSqlStatement.tables.getTable(0).toString());

//        System.out.println(sqlparser.sqlstatements.get(1).toString());
//
//        TGSqlParser parser = new TGSqlParser( EDbVendor.dbvoracle );
//        parser.sqltext = "set define off;\nupdate test set test_col='&VAR.' where test_where='someValue';\ncommit;";
//        int errorCode = parser.parse();
//        System.out.println(errorCode);
//        System.out.println(parser.getErrormessage());

    }

}
