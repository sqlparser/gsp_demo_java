package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
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
       // System.out.println(table.getTableName().getAnsiCatalogName());
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

    public void testImplicitDBSchema1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE DATABASE D2;\n" +
                "USE SCHEMA S2;\n" +
                "\n" +
                "CREATE TABLE mytable_unqualified (\n" +
                "emp_id INT,\n" +
                "dept_id INT,\n" +
                "name STRING);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createtable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(2);

        TTable table = createtable.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("mytable_unqualified"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("S2"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("D2"));
    }

    public void testImplicitDBSchema2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE DATABASE D2;\n" +
                "USE SCHEMA S2;\n" +
                "\n" +
                "CREATE TABLE foodmart.mytable_unqualified (\n" +
                "emp_id INT,\n" +
                "dept_id INT,\n" +
                "name STRING);";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createtable = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(2);

        TTable table = createtable.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("foodmart.mytable_unqualified"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("D2"));
    }

    public void testImplicitDBSchema3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE DATABASE D2;\n" +
                "USE SCHEMA S2;\n" +
                "INSERT INTO mytable_unqualified (emp_id, dept_id, name) VALUES (1, 1, 'dm_emp');";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(2);

        TTable table = insert.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("mytable_unqualified"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("S2"));

        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("D2"));
    }

    public void testImplicitDBSchema4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE DATABASE D2;\n" +
                "USE SCHEMA S2;\n" +
                "SELECT * FROM foodmart.mytable_unqualified;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(2);

        TTable table = select.getTables().getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("foodmart.mytable_unqualified"));
        assertTrue(table.getTableName().getSchemaString().equalsIgnoreCase("foodmart"));
        assertTrue(table.getTableName().getDatabaseString().equalsIgnoreCase(""));
        assertTrue(table.getTableName().getAnsiSchemaName().equalsIgnoreCase("foodmart"));

        assertTrue(table.getTableName().getAnsiCatalogName().equalsIgnoreCase("D2"));
    }

    public void testImplicitDBSchema5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "USE DATABASE D2;\n" +
                "USE SCHEMA S2;\n" +
                "CREATE OR REPLACE PROCEDURE my_simple_procedure()\n" +
                "RETURNS STRING\n" +
                "LANGUAGE JAVASCRIPT\n" +
                "EXECUTE AS CALLER\n" +
                "AS\n" +
                "'\n" +
                "var result = \"Hello, Snowflake!\";\n" +
                "return result;\n" +
                "';";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlparser.sqlstatements.get(2);

        TObjectName procedureName = createProcedure.getProcedureName();
        assertTrue(procedureName.toString().equalsIgnoreCase("my_simple_procedure"));
        assertTrue(procedureName.getSchemaString().equalsIgnoreCase(""));
        assertTrue(procedureName.getDatabaseString().equalsIgnoreCase(""));
        assertTrue(procedureName.getAnsiSchemaName().equalsIgnoreCase("S2"));
        assertTrue(procedureName.getAnsiCatalogName().equalsIgnoreCase("D2"));
    }

}
