package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.stmt.redshift.TRedshiftDropSchema;
import junit.framework.TestCase;

public class testDropSchema extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "drop schema s_sales restrict;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftDropSchema);
        TRedshiftDropSchema dropSchema = (TRedshiftDropSchema) sqlparser.sqlstatements.get(0);
        assertTrue(dropSchema.getNameList().getObjectName(0).toString().equalsIgnoreCase("s_sales"));


    }
}
