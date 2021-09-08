package mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExecuteSqlStatement;
import junit.framework.TestCase;

public class testExecute extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "execute mystmt";
        assertTrue(sqlparser.parse() == 0);

        TExecuteSqlStatement executeStmt = (TExecuteSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(executeStmt.getStatementName().toString().equalsIgnoreCase("mystmt"));
    }
}
