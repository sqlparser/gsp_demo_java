package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TRenameStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testRenameTable extends TestCase {

    public void testTableNameOnly(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "select name from cust x with (nolock)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.tables.getTable(0);
        table.getTableName().setString("customer");
        //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("select name from customer x with (nolock)"));
    }

    public void testRename1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "rename table s1.t1 to s2.t2";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt renameStmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(renameStmt.getOldName().getDatabaseString().equalsIgnoreCase("s1"));
        assertTrue(renameStmt.getOldName().getTableString().equalsIgnoreCase("t1"));
    }

    public void testTableNameInFrom(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select f from employee_schema__dm.employee ;";
        assertTrue(sqlparser.parse() == 0);

        TTable table = sqlparser.sqlstatements.get(0).getTables().getTable(0);
        TObjectName tableName = table.getTableName();
        assertTrue(tableName.getDatabaseString().equalsIgnoreCase("employee_schema__dm"));
        assertTrue(tableName.getTableString().equalsIgnoreCase("employee"));


    }

    public void testRenameList(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "rename table pet to pet2, event to event2;";
        assertTrue(sqlparser.parse() == 0);

        TRenameStmt renameStmt = (TRenameStmt)sqlparser.sqlstatements.get(0);
        assertTrue(renameStmt.getOldName().toString().equalsIgnoreCase("pet"));
        assertTrue(renameStmt.getNewName().toString().equalsIgnoreCase("pet2"));

        assertTrue(renameStmt.getRenamedObjectList().size() == 2);
        assertTrue(renameStmt.getRenamedObjectList().getElement(0).getObjectName(0).toString().equalsIgnoreCase("pet"));
        assertTrue(renameStmt.getRenamedObjectList().getElement(0).getObjectName(1).toString().equalsIgnoreCase("pet2"));
        assertTrue(renameStmt.getRenamedObjectList().getElement(1).getObjectName(0).toString().equalsIgnoreCase("event"));
        assertTrue(renameStmt.getRenamedObjectList().getElement(1).getObjectName(1).toString().equalsIgnoreCase("event2"));
    }

}
