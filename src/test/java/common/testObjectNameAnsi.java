package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testObjectNameAnsi extends TestCase {

    public void testNetezzaGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "SELECT id FROM foodmart.admin.department";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("admin"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("foodmart"));
    }

    public void testOracleGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT id FROM scott.emp@dblink";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("scott"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase(""));
    }

    public void testSQLServerGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT id FROM db1.dbo.emp";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("dbo"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("db1"));
    }

    public void testTeradataGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT id FROM foodmart.department";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase(""));
    }

    public void testMySQLGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT id FROM foodmart.department";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase(""));
    }

    public void testHiveGetAnsiSchemaName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT id FROM foodmart.department";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase(""));
    }

}
