package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateSchemaSqlStatement;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftCreateSchema;
import junit.framework.TestCase;



public class testCreateSchema extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create schema us_sales authorization dwuser;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateschema);
        TCreateSchemaSqlStatement createSchema = (TCreateSchemaSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createSchema.getSchemaName().toString().equalsIgnoreCase("us_sales"));
        assertTrue(createSchema.getOwnerName().toString().equalsIgnoreCase("dwuser"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create schema if not exists us_sales;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateschema);
        TCreateSchemaSqlStatement createSchema = (TCreateSchemaSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createSchema.getSchemaName().toString().equalsIgnoreCase("us_sales"));
    }

}