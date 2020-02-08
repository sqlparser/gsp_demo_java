package db2;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTablespaceStmt;
import junit.framework.TestCase;

public class testCreateTablespace extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CREATE LARGE TABLESPACE T";

        assertTrue(sqlparser.parse() == 0);

        TCreateTablespaceStmt tablespaceStmt = (TCreateTablespaceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(tablespaceStmt.getTablespaceName().toString().equalsIgnoreCase("T"));
    }
}
