package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.EAlterSchema;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftAlterSchema;
import junit.framework.TestCase;

public class testAlterSchema extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter schema sales rename to us_sales;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAlterSchema);
        TRedshiftAlterSchema schema = (TRedshiftAlterSchema) sqlparser.sqlstatements.get(0);
        assertTrue(schema.getAlterSchemaType() == EAlterSchema.easRenameTo);
        assertTrue(schema.getSchemaName().toString().endsWith("sales"));
        assertTrue(schema.getNewSchemaName().toString().endsWith("us_sales"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter schema us_sales owner to dwuser;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAlterSchema);
        TRedshiftAlterSchema schema = (TRedshiftAlterSchema) sqlparser.sqlstatements.get(0);
        assertTrue(schema.getAlterSchemaType() == EAlterSchema.easOwnerTo);
        assertTrue(schema.getSchemaName().toString().endsWith("us_sales"));
        assertTrue(schema.getOwnerName().toString().endsWith("dwuser"));
    }
}