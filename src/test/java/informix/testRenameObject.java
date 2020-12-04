package informix;
/*
 * Date: 13-1-25
 */

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TRenameStmt;
import gudusoft.gsqlparser.stmt.informix.TInformixDropRowTypeStmt;
import junit.framework.TestCase;

public class testRenameObject extends TestCase {

    public void testColumn(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "RENAME COLUMN customer.customer_num TO c_num;";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt stmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getOldName().toString().equalsIgnoreCase("customer.customer_num"));
        assertTrue(stmt.getNewName().toString().equalsIgnoreCase("c_num"));
        assertTrue(stmt.getObjectType() == EDbObjectType.column);
    }

    public void testIndex(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "RENAME index customer.customer_num TO c_num;";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt stmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getOldName().toString().equalsIgnoreCase("customer.customer_num"));
        assertTrue(stmt.getNewName().toString().equalsIgnoreCase("c_num"));
        assertTrue(stmt.getObjectType() == EDbObjectType.index);
    }

    public void testSequence(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "RENAME sequence customer.customer_num TO c_num;";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt stmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getOldName().toString().equalsIgnoreCase("customer.customer_num"));
        assertTrue(stmt.getNewName().toString().equalsIgnoreCase("c_num"));
        assertTrue(stmt.getObjectType() == EDbObjectType.sequence);
    }

    public void testTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "RENAME TABLE new_table TO items;";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt stmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getOldName().toString().equalsIgnoreCase("new_table"));
        assertTrue(stmt.getNewName().toString().equalsIgnoreCase("items"));
        assertTrue(stmt.getObjectType() == EDbObjectType.table);
    }
}
