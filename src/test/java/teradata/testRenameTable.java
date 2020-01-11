package teradata;

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TRenameStmt;
import junit.framework.TestCase;


public class testRenameTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "RENAME TABLE xyz.tbl1 to xyz.table1";
        assertTrue(sqlparser.parse() == 0);
        TRenameStmt renameStmt  = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(renameStmt.getObjectType() == EDbObjectType.table);
        assertTrue(renameStmt.getOldName().toString().equalsIgnoreCase("xyz.tbl1"));
        assertTrue(renameStmt.getNewName().toString().equalsIgnoreCase("xyz.table1"));

    }

}
