package mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExecutePreparedStatement;
import junit.framework.TestCase;

public class testExecute extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "execute mystmt";
        assertTrue(sqlparser.parse() == 0);

        TExecutePreparedStatement executeStmt = (TExecutePreparedStatement)sqlparser.sqlstatements.get(0);
        assertTrue(executeStmt.getStatementName().toString().equalsIgnoreCase("mystmt"));
    }
}
