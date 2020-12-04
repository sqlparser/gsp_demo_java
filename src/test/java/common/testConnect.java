package common;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;
import gudusoft.gsqlparser.stmt.TConnectStmt;
import junit.framework.TestCase;

public class testConnect  extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "CONNECT TO VERTICA ExampleDB USER dbadmin PASSWORD 'Password123' ON 'VerticaHost01',5433;";
        assertTrue(sqlparser.parse() == 0);
        TConnectStmt connectStmt = (TConnectStmt)sqlparser.sqlstatements.get(0);
        assertTrue(connectStmt.getDatabaseName().toString().equalsIgnoreCase("ExampleDB"));
        assertTrue(connectStmt.getUsername().toString().equalsIgnoreCase("dbadmin"));
        assertTrue(connectStmt.getPassword().toString().equalsIgnoreCase("'Password123'"));
        assertTrue(connectStmt.getHost().toString().equalsIgnoreCase("'VerticaHost01'"));
        assertTrue(connectStmt.getPort().toString().equalsIgnoreCase("5433"));
    }
}
