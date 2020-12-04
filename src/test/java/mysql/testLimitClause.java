package mysql;
/*
 * Date: 11-7-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLimitClause extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT * FROM whatever LIMIT 40, 5";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TLimitClause limitClause = selectSqlStatement.getLimitClause();
        assertTrue(limitClause.getOffset().toString().equalsIgnoreCase("40"));
        assertTrue(limitClause.getRow_count().toString().equalsIgnoreCase("5"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT * FROM whatever LIMIT 5";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TLimitClause limitClause = selectSqlStatement.getLimitClause();
        assertTrue(limitClause.getRow_count().toString().equalsIgnoreCase("5"));
    }
}
