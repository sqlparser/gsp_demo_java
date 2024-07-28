package common;
/*
 * Date: 13-11-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testTmp extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "set define off;\nupdate test set test_col='&VAR.' where test_where='someValue';\ncommit;";
        assertTrue(sqlparser.parse() == 0);
        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(1);

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        String input = "select upper(replace(replace(regexp_replace(test_column1,r'\\s|\\t|(|)|[!-/:-@[-`{-~]|[0-9A-Za-z]',''),'(',''),')','')) from t";
        sqlparser.sqltext = input;
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase(input));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        String input = "select upper(replace(replace(regexp_replace(test_column1,r'\\s|\\t|（|）|[!-/:-@[-`{-~]|[0-9A-Za-z]',''),'(',''),')','')) from t";
        sqlparser.sqltext = input;
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.toString().equalsIgnoreCase(input));
    }
}
