package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateSchemaSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateSchema extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE SCHEMA spence CREATE TABLE pet (name NVARCHAR(20), owner TEXT, species NTEXT, sex NCHAR(1), legs INT default 4, birth DATE, death DATE);";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("spence"));
        assertTrue(createSchemaSqlStatement.getBodyStatements().size() == 1);
        assertTrue(createSchemaSqlStatement.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)createSchemaSqlStatement.getBodyStatements().get(0);
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("pet"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE SCHEMA spence";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("spence"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE SCHEMA spence AUTHORIZATION ownername";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("spence"));
        assertTrue(createSchemaSqlStatement.getOwnerName().toString().equalsIgnoreCase("ownername"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE SCHEMA Sprockets AUTHORIZATION Annik\n" +
                "    CREATE TABLE NineProngs (source int, cost int, partnumber int)\n" +
                "    GRANT SELECT TO Mandar\n" +
                "    DENY SELECT TO Prasanna;";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("Sprockets"));
        assertTrue(createSchemaSqlStatement.getOwnerName().toString().equalsIgnoreCase("Annik"));

        assertTrue(createSchemaSqlStatement.getBodyStatements().size() == 3);
        assertTrue(createSchemaSqlStatement.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        assertTrue(createSchemaSqlStatement.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstGrant);
        assertTrue(createSchemaSqlStatement.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstmssqldeny);
    }

    public void testSnowflakeClone(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE SCHEMA demo.demo_table CLONE demo_stg.demo_table WITH MANAGED ACCESS;";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("demo.demo_table"));
        assertTrue(createSchemaSqlStatement.getCloneSourceSchema().toString().equalsIgnoreCase("demo_stg.demo_table"));
    }
}